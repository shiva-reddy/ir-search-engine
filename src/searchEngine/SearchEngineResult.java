package searchEngine;/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngineResult {

    public static class Metrics{
        Double cosineSimilarityScore, pageRankScore, combined;

        public Metrics(Double cosineSimilarityScore, Double pageRankScore,
                       PageRankCombinationStrategy combinationStrategy) {
            this.cosineSimilarityScore = cosineSimilarityScore;
            this.pageRankScore = pageRankScore;
            this.combined = combinationStrategy.combine(cosineSimilarityScore, pageRankScore);
        }
    }

    List<Map.Entry<String, Metrics>> documents = new ArrayList<>();

    SearchEngineResult(List<Map.Entry<String, Metrics>> documents){
        this.documents = documents;
    }

    public void print() {
        System.out.println("Result::");
        documents.subList(0,Integer.min(10, documents.size()))
                .forEach(entry -> System.out.println(entry.getKey()
                + " " + entry.getValue().cosineSimilarityScore +
                " " + entry.getValue().pageRankScore +
                " " + entry.getValue().combined
                ));
    }
}
