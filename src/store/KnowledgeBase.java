/* shiva created on 2/18/21 inside the package - com.solution */
package store;

import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import utilities.Constants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnowledgeBase implements Serializable {

    public final Map<String, Double> documentLengths;
    public final Map<String, Map<String, Integer>> termVsDocumentCount;
    public final VertexScoringAlgorithm<String, Double> pageRankScores;

    protected KnowledgeBase(Map<String, Double> documentLengths,
                            Map<String, Map<String, Integer>> termVsDocumentCount,
                            VertexScoringAlgorithm<String, Double> pageRankScores) {
        this.documentLengths = documentLengths;
        this.termVsDocumentCount = termVsDocumentCount;
        this.pageRankScores = pageRankScores;
    }

    public static KnowledgeBase getDefault() {
        return load(Constants.DEFAULT_KNOWLEDGE_BASE);
    }

    private static KnowledgeBase load(String path){
        return null;
    }
}
