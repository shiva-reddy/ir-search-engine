package webCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Collector;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Crawler {

    private ExecutorService executor;
    private AtomicInteger counter = new AtomicInteger();
    private List<Resource> pages = new LinkedList<>();
    private LinkedBlockingQueue<Future<Resource>> queue = new LinkedBlockingQueue();

    private ConcurrentMap<String, Integer> visitPageMap = new ConcurrentHashMap<>();
    private String domain;
    private String base = "dump/";

    public Crawler(String domain) {
        executor = Executors.newFixedThreadPool(5);
        this.domain = domain;
    }

    public static void main(String[] args) throws Exception {
        try {
            String startPage = "https://cs.uic.edu/";
            List<Resource> pages = new Crawler(startPage).crawl(startPage);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }


    public List<Resource> crawl(String link) throws InterruptedException, ExecutionException, IOException {
        visitSiteUrlIfNotVisitedBefore(link);

        //wait so queue is ready
        Thread.sleep(1000);

        while (!queue.isEmpty()) {
            Resource added = queue.poll().get();
            if(added != null){
                dumpResource(added);
                pages.add(added);
            }
        }
        executor.shutdown();
        return pages;
    }

    private void dumpResource(Resource res) throws IOException {
        FileOutputStream f = new FileOutputStream(new File(base + "" + res.id + ""));
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(res);
        o.close();
        f.close();
        System.out.println("Written " + res.id + " " + res.getLink());
        if(res.id > 3000) System.exit(0);
    }

    private void visitSiteUrlIfNotVisitedBefore(String visitSiteUrl) {
        try {
            final String url = normalize(visitSiteUrl);
            if (visitPageMap.putIfAbsent(url, 0) == null) {
                try {
                    Future<Resource> submit = executor.submit(() -> {
                        int x = counter.incrementAndGet();
                        try {
                            return visitLink(url, x);
                        } catch (IOException e) {
                            return null;
                        }
                    });
                    queue.put(submit);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            return;
        }
    }

    private String normalize(String url) {
        if(!url.contains("?") && !url.endsWith("/")) url += '/';
        return url;
    }

    public Resource visitLink(String link, int x) throws IOException {
        String uic = "uic.edu";
        Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .get();
        List<String> children = doc.select("a")
                .stream()
                .map(el -> el.attr("abs:href"))
                .map(l -> removeAnchor(l))
                .filter(l -> canVisit(l))
                .collect(Collectors.toList());
        Resource page = new Resource(x, link, children, doc.text());
        children.forEach(l -> visitSiteUrlIfNotVisitedBefore(l));
        return page;
    }

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz))$");

    private static boolean canVisit(String link){
        link = link.toLowerCase();
        return !FILTERS.matcher(link).matches()
                && link.contains("uic.edu");
    }

    public static String removeAnchor(String href) {
        int index = href.indexOf("#");
        if (index == -1) {
            return href;
        }
        return (href.substring(0, index));
    }

}

