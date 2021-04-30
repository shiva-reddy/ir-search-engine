package indexing;

import utilities.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public abstract class DocPreprocessor {

    StopWordEliminationStrategy stopWordEliminationCheck = new StopWordEliminator(Constants.DEFAULT_STOPWORDS_PATH);
    StemmingStrategy stemmingStrategy = new PorterStemmer();

    public abstract List<String> parseTokens(String filePath) throws IOException;

    protected List<String> tokenize(String text){
        List<String> tokens = new ArrayList<>();
        text = text.replaceAll("[^a-zA-Z ]", "").toLowerCase(); //Remove punctuation
        StringTokenizer tokenizer = new StringTokenizer(text, " "); //Split by whitespace
        while (tokenizer.hasMoreElements()) {
            String tok = tokenizer.nextToken();
            if(stopWordEliminationCheck.isStopWord(tok)) continue;
            tokens.add(stemmingStrategy.stemToken(tok));
        }
        return tokens;
    }
}
