/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import utilities.Constants;

import java.io.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = new File("").getAbsolutePath();
        if(filePath.endsWith("src")){
            Constants.DEFAULT_CRAWL_DESITNATION = "../" + Constants.DEFAULT_CRAWL_DESITNATION;
            Constants.DEFAULT_KNOWLEDGE_BASE = "../" + Constants.DEFAULT_KNOWLEDGE_BASE;
            Constants.DEFAULT_STOPWORDS_PATH = "../" + Constants.DEFAULT_STOPWORDS_PATH;
        }
        ConsoleMenu menu = new ConsoleMenu();
        menu.run();
    }
}
