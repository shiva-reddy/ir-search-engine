/* shiva created on 4/30/21 inside the package - indexing */
package indexing;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import store.InvertedIndex;
import store.KnowledgeBase;
import store.KnowledgeBaseBuilder;
import utilities.Utils;
import webCrawler.Resource;

import java.io.File;
import java.io.IOException;

public class Indexer {

    private final String collectionPath, kbDesitnation;
    private final KnowledgeBaseBuilder knowledgeBaseBuilder;

    public Indexer(String collectionPath, String kbDesitnation){
        this.collectionPath = collectionPath;
        this.kbDesitnation = kbDesitnation;
        knowledgeBaseBuilder =  new KnowledgeBaseBuilder();
    }

    public KnowledgeBase run() throws IOException, ClassNotFoundException {
        ResourceTextParser parser = new ResourceTextParser();
        ResourceMetaParser metaParser = new ResourceMetaParser();
        File folder = new File(collectionPath);

        for(File doc : folder.listFiles()){
            Resource resource = Resource.load(doc.getPath());
            loadGraphInfo(resource);
            loadTfIdfInfo(resource, parser, metaParser);
        }

        KnowledgeBase knowledgeBase = knowledgeBaseBuilder.build();
        Utils.dump(kbDesitnation, knowledgeBase);
        return knowledgeBase;
    }

    private void loadGraphInfo(Resource res){
        DefaultDirectedGraph<String, DefaultEdge> graph = knowledgeBaseBuilder.graph;
        if(!graph.containsVertex(res.getLink())) graph.addVertex(res.getLink());
        for(String neighbour : res.getExternalLinks()){
            if(!graph.containsVertex(neighbour)) graph.addVertex(neighbour);
            graph.addEdge(res.getLink(), neighbour);
        }
    }

    private void loadTfIdfInfo(Resource resource, ResourceTextParser textParser, ResourceMetaParser metaParser) throws IOException {
        String document = resource.getLink();
        textParser.parseTokens(resource)
                .forEach(token -> knowledgeBaseBuilder.updateTextStat(document, token));
//        metaParser.parseTokens(resource)
//                .forEach(token -> knowledgeBaseBuilder.updateHeaderStat(document, token));
    }

}
