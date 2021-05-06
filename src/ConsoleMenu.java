/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import indexing.Indexer;
import searchEngine.SearchEngine;
import store.KnowledgeBase;
import webCrawler.Crawler;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static utilities.Constants.DEFAULT_CRAWL_DESITNATION;
import static utilities.Constants.DEFAULT_SEED_URL;

public class ConsoleMenu {

    public void quickRun() throws Exception {
        searchQueryMenu(KnowledgeBase.getDefault());
    }

    public void runManual() throws Exception {
        while (true){
            printManualOptions();
            int input = readOptionInput();
            switch (input){
                case 1: {
                    crawlFromURLMenu();
                    break;
                }
                case 2: {
                    indexerMenu();
                    break;
                }
                case 3: {
                    searchQueryMenu(getKnowledgeBase());
                    break;
                }
                default: return;
            }
        }
    }

    private KnowledgeBase getKnowledgeBase() throws IOException, ClassNotFoundException {
        return KnowledgeBase.load(request("\tEnter the knowledge base index path: "));
    }

    private void printManualOptions(){
        System.out.println();
        System.out.println("\t1. Crawler");
        System.out.println("\t2. Indexer");
        System.out.println("\t3. Search engine");
        System.out.println("\tEnter option : ");
    }

    private void printQueryMenuOptions(){
        System.out.println();
        System.out.println("\t\tEnter your query (or 'q' to exit): ");
    }

    private void indexerMenu() throws Exception {
        System.out.println("\tEnter the collection path " );
        String collectionPath = readStringInput();
        System.out.println("\tEnter the index destination path " );
        String indexDestination = readStringInput();
        Indexer indexer = new Indexer(collectionPath, indexDestination);
        indexer.run();
    }

    private void searchQueryMenu(KnowledgeBase knowledgeBase) throws Exception {
        SearchEngine engine = new SearchEngine(KnowledgeBase.getDefault());
        while (true){
            printQueryMenuOptions();
            String query = readStringInput();
            if(query.equals("q")) return;
            engine.searchQuery(query).print();
        }
    }

    private String readStringInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private void crawlFromURLMenu() throws InterruptedException, ExecutionException, IOException {
        String seed = request("\t\tPlease enter the seed URL: ");
        int pagesToDownload = Integer.parseInt(request("\t\tPlease enter the number of pages to crawl: "));
        String destination = request("\t\tPlease enter the destination to save the crawled pages");
        int maxThreads = Integer.parseInt(request("\t\tPlease enter the maximum number of threads to use"));
        int sleepTimeout = Integer.parseInt(request(("\t\tPlease enter the milliseconds to sleep between requests")));
        Crawler crawler = new Crawler(seed, destination, pagesToDownload ,sleepTimeout,  maxThreads);
        crawler.run();
    }

    private String request(String question){
        System.out.println(question);
        return readStringInput();
    }

    private int readOptionInput() {
        Scanner scanner = new Scanner(System.in);
        try{
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e){
            return 0;
        }
    }

    public void run() throws Exception {
        System.out.println("1.Quick run");
        System.out.println("2.Manual");
        int option = readOptionInput();
        switch (option){
            case 1: {
                quickRun();
                break;
            }
            case 2:{
                runManual();
                break;
            }
        }
    }
}
