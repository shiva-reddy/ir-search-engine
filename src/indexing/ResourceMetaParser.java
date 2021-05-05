/* shiva created on 5/5/21 inside the package - indexing */
package indexing;

import webCrawler.Resource;

import java.io.IOException;
import java.util.List;

public class ResourceMetaParser extends DocPreprocessor{

    public List<String> parseTokens(Resource resource) throws IOException {
        String linkInfo = resource.getLink().replace('.', ' ')
                .replace('/', ' ')
                .replace('-', ' ');
        String data = "";
        data += multiply(linkInfo, 2);
        data += multiply(resource.getHeaderText(), 3);
        data += multiply(resource.getBoldText(), 3);
        return parseTokens(resource.getText());
    }

    private static String multiply(String text, int times){
        String res = "";
        for(int i = 0; i< times; i++){
            res += (" " + text + " ");
        }
        return res;
    }

    @Override
    public List<String> parseTokens(String content) throws IOException {
        return tokenize(content);
    }
}
