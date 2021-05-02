/* shiva created on 2/18/21 inside the package - com.solution */
package store;

import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;
import utilities.Constants;
import utilities.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnowledgeBase implements Serializable {

    public final Map<String, Double> documentLengths;
    public final Map<String, Map<String, Integer>> termVsDocumentCount;
    private final Map<String, Double> pageRankScores;

    protected KnowledgeBase(Map<String, Double> documentLengths,
                            Map<String, Map<String, Integer>> termVsDocumentCount,
                            VertexScoringAlgorithm<String, Double> pageRankScores) {
        this.documentLengths = documentLengths;
        this.termVsDocumentCount = termVsDocumentCount;
        this.pageRankScores = pageRankScores.getScores();
    }

    public Map<String, Double> getPageRankScores(){
        return pageRankScores;
    }

    public static KnowledgeBase getDefault() throws IOException, ClassNotFoundException {
        return load(Constants.DEFAULT_KNOWLEDGE_BASE);
    }

    private static KnowledgeBase load(String path) throws IOException, ClassNotFoundException {
        return (KnowledgeBase) Utils.load(path);
    }
}
