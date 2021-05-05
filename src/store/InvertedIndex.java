/* shiva created on 5/5/21 inside the package - store */
package store;

import java.io.Serializable;
import java.util.Map;

public class InvertedIndex implements Serializable {
    public final Map<String, Double> documentLengths;
    public final Map<String, Map<String, Integer>> termVsDocumentCount;

    public InvertedIndex(Map<String, Double> documentLengths, Map<String, Map<String, Integer>> termVsDocumentCount) {
        this.documentLengths = documentLengths;
        this.termVsDocumentCount = termVsDocumentCount;
    }
}
