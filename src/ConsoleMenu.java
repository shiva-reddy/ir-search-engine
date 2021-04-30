/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import searchEngine.SearchEngine;
import store.KnowledgeBase;
import webCrawler.Crawler;

import java.util.Scanner;

public class ConsoleMenu {

    public void run(){
        while (true){
            printMainOptions();
            int input = readOptionInput();
            switch (input){
                case 1: {
                    crawlFromURLMenu();
                    break;
                }
                case 2: {
                    searchQueryMenu();
                    break;
                }
                default: return;
            }
        }
    }

    private void printMainOptions(){
        System.out.println("1. Crawl from URL");
        System.out.println("2. Search queries");
        System.out.println("Enter option : ");
    }

    private void printQueryMenuOptions(){
        System.out.println("\tEnter your query (or 'q' to exit): ");
    }

    private void searchQueryMenu() {
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

    private void crawlFromURLMenu() {
        System.out.println("\tEnter the seed URL: ");
        String urlInput = readStringInput();
        KnowledgeBase dump = Crawler.crawl(urlInput);
        System.out.println("\nDumped after crawling at : " + dump.fileName);
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
