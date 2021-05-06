package webCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utilities.Constants;
import utilities.Utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Crawler {

    private ThreadPoolExecutor executor;
    Collection<Future<?>> futures = new LinkedList<Future<?>>();
    private AtomicInteger counter = new AtomicInteger();
    private ConcurrentMap<String, Integer> visitPageMap = new ConcurrentHashMap<>();
    private String domain;
    private String base;
    private final int limit, sleepTime;

    public Crawler(String domain, String destination, int limit,  int sleepTime, int maxThreads) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxThreads);
        this.domain = domain;
        this.base = destination;
        File f = new File(base);
        if(!f.exists()) f.mkdir();
        this.sleepTime = sleepTime;
        this.limit = limit;
    }

    public void run() throws InterruptedException, ExecutionException, IOException {
        scheduleLinkVisit(normalize(domain));
        while (counter.get() <= limit);
        for (Future<?> future : futures) future.get();
        Utils.dump("crawled_pages", (Serializable) visitPageMap.keySet());
        Thread.sleep(sleepTime);
    }

    private void dumpResource(Resource res) throws IOException {
        Utils.dump(base + "" + res.id + "", res);
        System.out.println("Downloaded " + res.id + " " + res.getLink());
    }

    private void scheduleLinkVisit(String link) {
        if(!isValidDomain(link)) return;
        int id = counter.incrementAndGet();
        if(id > limit || visitPageMap.containsKey(link)) return;
        visitPageMap.put(link, id);
        futures.add(executor.submit(() -> {
            try {
                Resource resource = fetchResource(link, id);
                dumpResource(resource);
                resource.getSubDomainLinks().forEach(l -> scheduleLinkVisit(normalize(l)));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Skipped " + link);
            }
        }));
    }

    public static String normalize(String url) {
        if(url.isEmpty()) return url;
        url = removeAnchor(url);
        if(url.endsWith("/")) url = url.substring(0, url.length() - 1);
        String mainDomain = url.substring(url.indexOf("//") + 2);
        mainDomain = mainDomain.contains("www.") ? mainDomain.substring(mainDomain.indexOf("www.") + 4)
                : mainDomain;
        url = "https://" + mainDomain;
        url = (!url.contains("?") && !url.endsWith("/")) ? url + '/' : url;
        return url;
    }

    public Resource fetchResource(String link, int id) throws IOException {
        String uic = "uic.edu";
        Document doc = Jsoup.connect(link)
                .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                .get();

        Set<String> allExternalLinks =  doc.select("a")
                .stream()
                .map(el -> el.attr("abs:href").toLowerCase())
                .filter(l -> l.startsWith("http") && !FILTERS.matcher(link).matches())
                .collect(Collectors.toSet());

        Set<String> subdomainLinks = allExternalLinks.stream()
                .filter(l -> isValidDomain(link))
                .collect(Collectors.toSet());

        String headerText = doc.select("h1").text()
                + " "
                + doc.select("h2").text()
                + " "
                + doc.select("h3").text()
                + " "
                + doc.select("h4").text();

        String boldText = doc.select("b").text();
        return new Resource(id, link, allExternalLinks, subdomainLinks, doc.text(), headerText, boldText);
    }

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz|pdf))$");

    public static boolean isValidDomain(String link) {
        try{
            URL url = new URL(link);
            return url.getHost().contains("uic.edu");
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    public static String removeAnchor(String href) {
        return href.contains("#") ? href.substring(0, href.indexOf("#")) : href;
    }

}

