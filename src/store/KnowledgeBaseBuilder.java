/* shiva created on 4/30/21 inside the package - store */
package store;

import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import utilities.Utils;

import java.io.IOException;
import java.util.*;

import static utilities.Utils.log2;

public class KnowledgeBaseBuilder {

    public final Map<String, Map<String, Integer>> termVsDocumentCount = new HashMap<>();
    public final Set<String> documents = new HashSet<>();
    public final Map<String, Integer> tokenVsTotalCount = new HashMap<>();
    public final DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);

    public void updateStat(String document, String term){
        documents.add(document);
        tokenVsTotalCount.put(term, tokenVsTotalCount.getOrDefault(term, 0) + 1);
        if(!termVsDocumentCount.containsKey(term)) termVsDocumentCount.put(term, new HashMap<>());
        termVsDocumentCount.get(term)
                .put(document, termVsDocumentCount.get(term)
                        .getOrDefault(document, 0) + 1);
    }

    public KnowledgeBase build() throws IOException {
        return new KnowledgeBase(computeDocumentLengths(), termVsDocumentCount, new PageRank<>(graph));
    }

    private Map<String, Double> computeDocumentLengths() {
        Map<String, Double> res = new HashMap<>();
        int N = documents.size();
        List<Double> vals = new ArrayList<>();
        termVsDocumentCount
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

}
