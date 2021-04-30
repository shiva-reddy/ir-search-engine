/*
    @author: Shiva Reddy Kokilathota Jagirdar
 */
package indexing;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class StopWordEliminator implements StopWordEliminationStrategy {

    private Set<String> stopWords = new HashSet<>();

    StopWordEliminator(String path) {
        try {
            Scanner scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine());
            }
            scanner.close();
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean isStopWord(String word) {
        return stopWords.contains(word);
    }
}
