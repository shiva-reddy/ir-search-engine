/* shiva created on 2/18/21 inside the package - com.solution */
package indexing;

import java.io.IOException;
import java.util.List;

public class QueryParser extends DocPreprocessor{

    @Override
    public List<String> parseTokens(String input) throws IOException {
        return tokenize(input);
    }

    public static List<String> parse(String input) throws IOException {
        QueryParser queryParser = new QueryParser();
        return queryParser.parseTokens(input);
    }
}
