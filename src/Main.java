/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import searchEngine.SearchEngine;
import searchEngine.SearchEngineResult;
import store.KnowledgeBase;
import utilities.Utils;
import webCrawler.Crawler;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static webCrawler.Crawler.normalize;

public class Main {
    public static void main(String[] args) throws Exception {
//        Evaluation.runEvaluations();
        ConsoleMenu menu = new ConsoleMenu();
        menu.run();
    }
}
