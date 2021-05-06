/* shiva created on 5/5/21 inside the package - PACKAGE_NAME */

import searchEngine.SearchEngine;
import searchEngine.SearchEngineResult;
import store.KnowledgeBase;
import utilities.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static webCrawler.Crawler.normalize;

public class Evaluation {

    private static class EvaluationMetric{
        String query;
        Double precision, recall;

        public EvaluationMetric(String query, Double precision, Double recall) {
            this.query = query;
            this.precision = precision;
            this.recall = recall;
        }
    }

    public static void runEvaluations() throws IOException, ClassNotFoundException {
        String base = "evaluations/google-search-outputs/";
        List<String> queries = Arrays.asList("Course schedule",
                "computer science",
                "Library",
                "Cryptography",
                "Maps");
        Set<String> crawled = (Set) Utils.load("crawled_pages");
        List<EvaluationMetric> results = queries.
                stream()
                .map(query -> evaluate(query, base + query, crawled))
                .collect(Collectors.toList());
        results.forEach(result -> System.out.println(result.query + " | " + result.precision + " | " + result.recall));
    }

    private static EvaluationMetric evaluate(String query, String externalResultPath, Set<String> crawled) {
        try {
            List<String> urlsInExternalSite = loadURLs(externalResultPath)
                    .stream()
                    .map(url -> normalize(url))
                    .filter(url -> crawled.contains(url))
                    .collect(Collectors.toList());
            System.out.println("Query " + query  + " | " + urlsInExternalSite.size());
            if(urlsInExternalSite.size() == 0) return new EvaluationMetric(query, 0.0d, 0.0d);
            SearchEngine searchEngine = new SearchEngine(KnowledgeBase.getDefault());
            SearchEngineResult result = searchEngine.searchQuery(query);
            result.print();
            Double count = 0.0d;
            for (Map.Entry<String, SearchEngineResult.Metrics> entry : result.getDocuments()) {
                String document = entry.getKey();
                if (urlsInExternalSite.contains(document)) {
                    count += 1;
                }
            }
            return new EvaluationMetric(query, count / result.getDocuments().size(),
                    count / urlsInExternalSite.size());
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static List<String> loadURLs(String externalResultPath) throws Exception {
        List<String> urls = new ArrayList<>();
        FileInputStream f = new FileInputStream(externalResultPath);
        BufferedReader br = new BufferedReader(new InputStreamReader(f));
        String line;
        while ((line = br.readLine()) != null)   urls.add(line);
        f.close();
        return urls;
    }
}
