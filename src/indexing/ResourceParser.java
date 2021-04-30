/* shiva created on 4/30/21 inside the package - indexing */
package indexing;

import webCrawler.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ResourceParser extends DocPreprocessor {

    public List<String> parseTokens(Resource resource) throws IOException {
        return parseTokens(resource.getText());
    }

    @Override
    public List<String> parseTokens(String content) throws IOException {
        return tokenize(content);
    }
}
