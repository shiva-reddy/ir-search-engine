/* shiva created on 2/18/21 inside the package - com.solution */
package store;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnowledgeBase {

    public final Map<String, Map<String, Integer>> termVsDocumentCount = new HashMap<>();
    public final Set<String> documents = new HashSet<>();
    public final Map<String, Integer> tokenVsTotalCount = new HashMap<>();
    public String fileName;

    public static KnowledgeBase getDefault() {
    }


    public void updateStat(String document, String term){
        documents.add(document);
        tokenVsTotalCount.put(term, tokenVsTotalCount.getOrDefault(term, 0) + 1);
        if(!termVsDocumentCount.containsKey(term)) termVsDocumentCount.put(term, new HashMap<>());
        termVsDocumentCount.get(term)
                .put(document, termVsDocumentCount.get(term)
                        .getOrDefault(document, 0) + 1);
    }
}
