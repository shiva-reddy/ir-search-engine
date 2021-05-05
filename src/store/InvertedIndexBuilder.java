/* shiva created on 5/5/21 inside the package - store */
package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utilities.Utils.log2;

public class InvertedIndexBuilder {
    public final Map<String, Map<String, Integer>> termVsDocumentCount = new HashMap<>();

    public InvertedIndex build(int noOfDocuments){
        return new InvertedIndex(computeDocumentLengths(noOfDocuments), termVsDocumentCount);
    }

    private Map<String, Double> computeDocumentLengths(int noOfDocuments) {
        Map<String, Double> res = new HashMap<>();
        int N = noOfDocuments;
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
