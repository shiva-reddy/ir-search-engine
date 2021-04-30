/* shiva created on 2/17/21 inside the package - com.solution */
package indexing;

import store.KnowledgeBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static String stopWordsPath = "../stopwords.txt";


    private static Map<Integer, Set<String>> getRelvantDocs(String path) throws IOException {
        List<String> lines =  Files.readAllLines(Paths.get(path));
        Map<Integer, Set<String>> res = new HashMap<>();
        for(String line : lines){
            String[] split = line.split(" ");
            Integer query = Integer.valueOf(split[0]) - 1;
            if(!res.containsKey(query)) res.put(query, new HashSet<>());
            res.get(query).add(split[1]);
        }
        return res;
    }

    private static void run(String collectionPath, String queriesPath, String relevantDocsPath, boolean printList) throws IOException {
        KnowledgeBase documentIndex = createDocumentIndex(collectionPath);
        Map<String, Double> documentLengths = computeDocumentLengths(documentIndex);
        Map<Integer, Set<String>> queryVsRelevantDocuments = getRelvantDocs(relevantDocsPath);
        QueryParser queryParser = new QueryParser();

        List<String> queries = getQueries(queriesPath);
        List<Integer> kValues = Arrays.asList(10,50,100,500);

        double[][] precision = new double[queries.size()][kValues.size()];
        double[][] recall = new double[queries.size()][kValues.size()];

        for(int i = 0; i < queries.size(); i++){
            String query = queries.get(i);
            Map<String, Integer> tokenCounts = getCountMap(queryParser.parseTokens(query));
            Map<String, Double> documentCosine = computeCosineSimilarityNumerators(documentIndex, tokenCounts);
            divideByDocumentLengths(documentCosine, documentLengths);
            List<Map.Entry<String, Double>> rankedDocuments = new ArrayList<>();
            rankedDocuments.addAll(documentCosine.entrySet());
            if(printList){
                final int _i = i + 1;
                rankedDocuments.forEach(doc -> System.out.println("(" + _i + ", " + doc.getKey() + ")"));
            }
            Collections.sort(rankedDocuments, (a, b) -> Double.compare(b.getValue(), a.getValue()));
            for(int j = 0; j < kValues.size(); j++) {
                precision[i][j] = calculatePrecision(queryVsRelevantDocuments.get(i), rankedDocuments, kValues.get(j));
                recall[i][j] = calculateRecall(queryVsRelevantDocuments.get(i), rankedDocuments, kValues.get(j));
            }
        }

        for(int i = 0; i < queries.size(); i++){
            System.out.println("For Query  " + (i + 1));
            for(int j = 0; j < kValues.size(); j++){
                System.out.println("\tk = " + kValues.get(j));
                System.out.println("\t\tPrecision => " + precision[i][j]);
                System.out.println("\t\tRecall => " + recall[i][j]);
            }
        }

        System.out.println();
        System.out.println("Average precision and recall");
        for(int j = 0; j < kValues.size(); j++){
            double totalPrecision = 0.0d, totalRecall = 0.0d;
            for(int i = 0; i < queries.size(); i++){
                totalPrecision += precision[i][j];
                totalRecall += recall[i][j];
            }
            System.out.println("k = " + kValues.get(j));
            System.out.println("\tAverage precision => " + (totalPrecision / queries.size()));
            System.out.println("\tAverage Recall => " + (totalRecall / queries.size()));
            System.out.println();
        }
    }

    private static double calculateRecall(Set<String> relaventDocs, List<Map.Entry<String, Double>> rankedDocuments, Integer k) {
        Set<String> topK = new HashSet<>();
        for(int i = 0; i < k; i++) topK.add(rankedDocuments.get(i).getKey());
        int count = 0;
        for(String doc : relaventDocs) if(topK.contains(doc)) count++;
        return (double) count / relaventDocs.size();
    }

    private static double calculatePrecision(Set<String> relaventDocs, List<Map.Entry<String, Double>> rankedDocuments, Integer k) {
        Set<String> topK = new HashSet<>();
        for(int i = 0; i < k; i++) topK.add(rankedDocuments.get(i).getKey());
        int count = 0;
        for(String doc : relaventDocs) if(topK.contains(doc)) count++;
        return (double) count / k;
    }

    private static void divideByDocumentLengths(Map<String, Double> documentCosine, Map<String, Double> documentLengths) {
        documentCosine
                .entrySet()
                .forEach(entry ->
                        documentCosine.put(entry.getKey(), entry.getValue() / documentLengths.get(entry.getKey())));
    }

    private static Map<String, Double> computeCosineSimilarityNumerators(KnowledgeBase documentIndex,
                                                                         Map<String, Integer> queryTokenCounts) {
        Map<String, Double> res = new HashMap<>();
        double N = documentIndex.documents.size();
        queryTokenCounts.entrySet()
                .forEach(entry -> {
                    String term = entry.getKey();
                    Integer countInQuery = entry.getValue();
                    Map<String, Integer> documentsWithTerm = documentIndex.termVsDocumentCount.getOrDefault(term, new HashMap<>());
                    documentsWithTerm.entrySet()
                            .forEach(_entry -> {
                                String document = _entry.getKey();

                                Double docTF = _entry.getValue().doubleValue();
                                Double docIDF = log2(N / documentsWithTerm.size());
                                Double docWeight = docTF * docIDF;

                                Double queryTF = queryTokenCounts.get(term).doubleValue();
                                Double queryIDF = log2(N / documentsWithTerm.size());
                                Double queryWeight = queryTF * queryIDF;

                                Double weightTerm = docWeight * queryWeight;

                                res.put(document, res.getOrDefault(document, 0.0d) + weightTerm);
                            });
                });
        return res;
    }

    private static double log2(double val){
        return Math.log(val) / Math.log(2.0d);
    }

    private static Map<String, Double> computeDocumentLengths(KnowledgeBase documentIndex) {
        Map<String, Double> res = new HashMap<>();
        int N = documentIndex.documents.size();
        List<Double> vals = new ArrayList<>();
        documentIndex.termVsDocumentCount
                .entrySet()
                .forEach(entry -> {
                    String term = entry.getKey();
                    Map<String, Integer> documentsWithTerm = entry.getValue();
                    documentsWithTerm
                            .entrySet()
                            .forEach(_entry -> {
                                String document = _entry.getKey();
                                Double docTF = _entry.getValue().doubleValue();
                                Double docIDF = log2(N / documentsWithTerm.size());
                                Double weight =  docTF * docIDF;
                                vals.add(Math.pow(weight, 2));
                                res.put(document, res.getOrDefault(document, 0.0d) + Math.pow(weight, 2));
                            });
                });
        res.entrySet().forEach(entry -> res.put(entry.getKey(), Math.sqrt(entry.getValue())));
        return res;
    }

    private static Map<String, Integer> getCountMap(List<String> tokens) {
        Map<String, Integer> countMap = new HashMap<>();
        tokens.forEach(tok -> countMap.put(tok , countMap.getOrDefault(tok, 0) + 1));
        return countMap;
    }

    private static List<String> getQueries(String queriesPath) throws IOException {
        return Files.readAllLines(Paths.get(queriesPath));
    }

    private static KnowledgeBase createDocumentIndex(String collectionPath) throws IOException {
        KnowledgeBase st = new KnowledgeBase();
        CranFieldDocParser parser = new CranFieldDocParser();
        File folder = new File(collectionPath);
        for(File fileEntry : folder.listFiles()){
            String docId = Integer.valueOf(fileEntry.getName().substring(9)).toString();
            parser.parseTokens(fileEntry.getPath())
                    .forEach(token -> st.updateStat(docId, token));

        }
        return st;
    }


}
