/* shiva created on 4/30/21 inside the package - store */
package store;

import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.IOException;
import java.util.*;

public class KnowledgeBaseBuilder {

    public final Set<String> documents = new HashSet<>();
    public final DefaultDirectedGraph<String, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
    public final InvertedIndexBuilder textIndexBuilder = new InvertedIndexBuilder(),
                                      headerIndexBuilder = new InvertedIndexBuilder();

    public void updateStat(String documentID, String term, InvertedIndexBuilder index){
        if(!index.termVsDocumentCount.containsKey(term)) index.termVsDocumentCount.put(term, new HashMap<>());
        index.termVsDocumentCount.get(term)
                .put(documentID, index.termVsDocumentCount.get(term)
                        .getOrDefault(documentID, 0) + 1);
    }

    public void updateHeaderStat(String documentID, String term){
        documents.add(documentID);
        updateStat(documentID, term, headerIndexBuilder);
    }

    public void updateTextStat(String documentID, String term){
        documents.add(documentID);
        updateStat(documentID, term, textIndexBuilder);
    }

    public KnowledgeBase build() throws IOException {
        System.out.println(documents.size());
        return new KnowledgeBase(textIndexBuilder.build(documents.size()), headerIndexBuilder.build(documents.size()), new PageRank<>(graph));
    }

}
