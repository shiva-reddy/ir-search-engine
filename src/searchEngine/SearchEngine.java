package searchEngine;/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import indexing.QueryParser;
import store.InvertedIndex;
import store.KnowledgeBase;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static utilities.Utils.*;

public class SearchEngine {

    private final KnowledgeBase knowledgeBase;

    public SearchEngine(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public SearchEngineResult searchQuery(String query) throws IOException {

        Map<String, Integer> queryTokenCounts = getCountMap(QueryParser.parse(query));
        System.out.println(queryTokenCounts.toString());
        System.out.println(knowledgeBase.getHeaderInvertedIdx().termVsDocumentCount.toString());
        Map<String, Double> documentVsCosineScore = computeCosineSimilarity(queryTokenCounts, knowledgeBase.getTextInvertedIdx());
        Map<String, Double> top10 = filterTop(documentVsCosineScore, 10, (d1, d2) -> Double.compare(d1, d2));

        for(Map.Entry<String, Double> e : top10.entrySet()) System.out.println(e.getKey() + " " + e.getValue());
        System.out.println("-----------------");
        Map<String, Double> documentVsPageRankScores = knowledgeBase.getPageRankScores();

        return new SearchEngineResult(computeResult(documentVsCosineScore, documentVsPageRankScores));
    }

    private Map<String, Double> getCombinedCosineScore(Map<String, Integer> queryTokenCounts){
        CosineSimilarityCombinationStrategy strategy = new CosineSimilarityCombinationStrategy();
        Map<String, Double> headerCosineScores = computeCosineSimilarity(queryTokenCounts, knowledgeBase.getHeaderInvertedIdx());
        Map<String, Double> textCosineScores = computeCosineSimilarity(queryTokenCounts, knowledgeBase.getTextInvertedIdx());
        Map<String, Double> combinedCosineScores = new HashMap<>();
        textCosineScores.keySet().forEach(document -> combinedCosineScores.put(document,
                strategy.combine(headerCosineScores.get(document), textCosineScores.get(document))));
        return combinedCosineScores;
    }

    private List<Map.Entry<String, SearchEngineResult.Metrics>> computeResult(Map<String, Double> documentVsCosine,
                                                                              Map<String, Double> documentVsPageRankScores) {
        Map<String, Double> topDocsCosine = fetchTopDocs(documentVsCosine);
        Map<String, Double> topDocsPageRank = filterAndNormalize(topDocsCosine.keySet(), knowledgeBase.getPageRankScores());

        PageRankCombinationStrategy pageRankCombinationStrategy = new PageRankCombinationStrategy();
        Map<String, SearchEngineResult.Metrics> resultMetrics = topDocsCosine.keySet()
                .stream()
                .collect(Collectors.toMap(
                        document -> document,
                        document -> new SearchEngineResult.Metrics(topDocsCosine.get(document),
                                                                   topDocsPageRank.get(document),
                                                                    pageRankCombinationStrategy)));
        List<Map.Entry<String, SearchEngineResult.Metrics>> sortedList = new ArrayList<>(resultMetrics.entrySet());
        Collections.sort(sortedList, (e1 , e2) -> Double.compare(e1.getValue().combined, e1.getValue().combined));
        return sortedList;
    }

    private Map<String, Double> filterAndNormalize(Set<String> documents,
                                                   Map<String, Double> pageRankScores) {
        return normalize(documents
                .stream()
                .collect(Collectors.toMap(document -> document, document -> pageRankScores.get(document))));
    }

    private Map<String, Double> fetchTopDocs(Map<String, Double> documentVsCosine){
        List<Map.Entry<String, Double>> rankedDocuments = new ArrayList<>(documentVsCosine.entrySet());
        Collections.sort(rankedDocuments, (e1, e2) -> Double.compare(e1.getValue(), e2.getValue()));
        return rankedDocuments.subList(0, Math.min(10, rankedDocuments.size()))
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private Map<String, Integer> getCountMap(List<String> tokens) {
        Map<String, Integer> countMap = new HashMap<>();
        tokens.forEach(tok -> countMap.put(tok , countMap.getOrDefault(tok, 0) + 1));
        return countMap;
    }

    private Map<String, Double> computeCosineSimilarity(Map<String, Integer> queryTokenCounts, InvertedIndex invertedIndex) {
        Map<String, Double> res = new HashMap<>();
        double N = invertedIndex.documentLengths.size();
        queryTokenCounts.entrySet()
                .forEach(entry -> {
                    String term = entry.getKey();
                    Integer countInQuery = entry.getValue();
                    Map<String, Integer> documentsWithTerm = invertedIndex
                            .termVsDocumentCount.getOrDefault(term, new HashMap<>());
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
        divideByDocumentLengths(res, invertedIndex.documentLengths);
        return res;
    }

    private static void divideByDocumentLengths(Map<String, Double> documentCosine, Map<String, Double> documentLengths) {
        documentCosine
                .entrySet()
                .forEach(entry ->
                        documentCosine.put(entry.getKey(), entry.getValue() / documentLengths.get(entry.getKey())));
    }
}
