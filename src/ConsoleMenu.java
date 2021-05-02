/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import indexing.Indexer;
import searchEngine.SearchEngine;
import store.KnowledgeBase;
import webCrawler.Crawler;
import webCrawler.Resource;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import static utilities.Constants.DEFAULT_CRAWL_DESITNATION;
import static utilities.Constants.DEFAULT_SEED_URL;

public class ConsoleMenu {

    public void run() throws Exception {
        while (true){
            printMainOptions();
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
                    searchQueryMenu();
                    break;
                }
                default: return;
            }
        }
    }

    private void printMainOptions(){
        System.out.println("1. Crawler");
        System.out.println("2. Indexer");
        System.out.println("3. Search engine");
        System.out.println("Enter option : ");
    }

    private void printQueryMenuOptions(){
        System.out.println("\tEnter your query (or 'q' to exit): ");
    }

    private void indexerMenu() throws Exception {
        System.out.println("\tEnter the collection path " );
        String collectionPath = readStringInput();
        System.out.println("\tEnter the index destination path " );
        String indexDestination = readStringInput();
        Indexer indexer = new Indexer(collectionPath, indexDestination);
        indexer.run();
    }

    private void searchQueryMenu() throws Exception {
        SearchEngine engine = new SearchEngine(KnowledgeBase.getDefault());
        while (true){
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
//        System.out.println("\tEnter the seed URL: ");
//        String urlInput = readStringInput();
//        System.out.println("\tEnter the destination to store crawled pages: ");
//        String destination = readStringInput();
        System.out.println(DEFAULT_CRAWL_DESITNATION);
        Crawler crawler = new Crawler(DEFAULT_SEED_URL, DEFAULT_CRAWL_DESITNATION);
        crawler.run();
        System.out.println("\nDumped after crawling at : " + DEFAULT_CRAWL_DESITNATION);
    }

    private int readOptionInput() {
        Scanner scanner = new Scanner(System.in);
        try{
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e){
            return 0;
        }
    }
}
