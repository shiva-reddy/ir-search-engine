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

public class ConsoleMenu {

    public void run() throws InterruptedException, ExecutionException, IOException {
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

    private void indexerMenu() throws IOException {
        System.out.println("\tEnter the collection path " );
        String collectionPath = readStringInput();
        System.out.println("\tEnter the index destination path " );
        String indexDestination = readStringInput();
        Indexer indexer = new Indexer(collectionPath, indexDestination);
        indexer.run();
    }

    private void searchQueryMenu() throws IOException {
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
        System.out.println("\tEnter the seed URL: ");
        String urlInput = readStringInput();
        System.out.println("\tEnter the destination to store crawled pages: ");
        String destination = readStringInput();
        Crawler crawler = new Crawler(urlInput, destination);
        crawler.run();
        System.out.println("\nDumped after crawling at : " + destination);
    }

    private int readOptionInput() {
        Scanner scanner = new Scanner(System.in);
        try{
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e){
            return 0;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            String startPage = "https://cs.uic.edu/";
            Crawler crawler = new Crawler(startPage, "dump/");
            crawler.run();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
