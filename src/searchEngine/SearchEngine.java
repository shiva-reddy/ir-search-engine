package searchEngine;/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import indexing.QueryParser;
import store.KnowledgeBase;

import java.io.IOException;
import java.util.*;

import static utilities.Utils.log2;

public class SearchEngine {

    private final KnowledgeBase knowledgeBase;

    public SearchEngine(KnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public SearchEngineResult searchQuery(String query) throws IOException {
        QueryParser queryParser = new QueryParser();

        Map<String, Integer> queryTokenCounts = getCountMap(queryParser.parseTokens(query));

        Map<String, Double> documentVsCosine = computeCosineSimilarityNumerators(queryTokenCounts);
        Map<String, Double> documentVsPageRankScores = knowledgeBase.getPageRankScores();

        return new SearchEngineResult(computeResult(documentVsCosine, documentVsPageRankScores));
    }

    private List<Map.Entry<String, Double>> computeResult(Map<String, Double> documentVsCosine,
                                              Map<String, Double> documentVsPageRankScores) {
        Map<String, Double> result = new HashMap<>();

        for(String doc : documentVsCosine.keySet()){
            result.put(doc, combineParams(documentVsCosine.get(doc), documentVsPageRankScores.get(doc)));
        }

        List<Map.Entry<String, Double>> rankedDocuments = new ArrayList<>(result.entrySet());
        Collections.sort(rankedDocuments, (a, b) -> Double.compare(b.getValue(), a.getValue()));

        return rankedDocuments;
    }

    private Double combineParams(Double cosineSimilarityScore, Double pageRankScore){
        return cosineSimilarityScore + pageRankScore;
    }

    private Map<String, Integer> getCountMap(List<String> tokens) {
        Map<String, Integer> countMap = new HashMap<>();
        tokens.forEach(tok -> countMap.put(tok , countMap.getOrDefault(tok, 0) + 1));
        return countMap;
    }

    private Map<String, Double> computeCosineSimilarityNumerators(Map<String, Integer> queryTokenCounts) {
        Map<String, Double> res = new HashMap<>();
        double N = knowledgeBase.documentLengths.size();
        queryTokenCounts.entrySet()
                .forEach(entry -> {
                    String term = entry.getKey();
                    Integer countInQuery = entry.getValue();
                    Map<String, Integer> documentsWithTerm = knowledgeBase
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
        divideByDocumentLengths(res, knowledgeBase.documentLengths);
        return res;
    }

    private static void divideByDocumentLengths(Map<String, Double> documentCosine, Map<String, Double> documentLengths) {
        documentCosine
                .entrySet()
                .forEach(entry ->
                        documentCosine.put(entry.getKey(), entry.getValue() / documentLengths.get(entry.getKey())));
    }
}
