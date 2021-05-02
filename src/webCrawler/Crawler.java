package webCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import utilities.Constants;
import utilities.Utils;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
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
    private final int limit;

    public Crawler(String domain, String destination, int limit) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        this.domain = domain;
        this.base = destination;
        this.limit = limit;
    }

    public Crawler(String domain, String destination) {
        this(domain, destination, Constants.DEFAULT_DOCUMENT_LIMIT);
    }

    public void run() throws InterruptedException, ExecutionException, IOException {
        scheduleLinkVisit(normalize(domain));
        while (counter.get() <= limit);
        for (Future<?> future : futures) future.get();
    }

    private void dumpResource(Resource res) throws IOException {
        Utils.dump(base + "" + res.id + "", res);
        System.out.println("Downloaded " + res.id + " " + res.getLink());
    }

    private void scheduleLinkVisit(String link) {
        int id = counter.incrementAndGet();
        if(id > limit || visitPageMap.containsKey(link)) return;
        visitPageMap.put(link, id);
        futures.add(executor.submit(() -> {
            try {
                Resource resource = fetchResource(link, id);
                dumpResource(resource);
                resource.getChildren().forEach(l -> scheduleLinkVisit(l));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private String normalize(String url) {
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
        Set<String> children = doc.select("a")
                .stream()
                .map(el -> el.attr("abs:href").toLowerCase())
                .filter(l -> l.startsWith("http") &&
                             !FILTERS.matcher(link).matches() &&
                             isValidDomain(link))
                .map(l -> normalize(l))
                .collect(Collectors.toSet());
        return new Resource(id, link, children, doc.text());
    }

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp4|zip|gz|pdf))$");

    private boolean isValidDomain(String link) {
        link = link.substring(link.indexOf("//") + 2);
        link = link.substring(0, link.indexOf("/"));
        return link.contains("uic.edu");
    }

    public static String removeAnchor(String href) {
        return href.contains("#") ? href.substring(0, href.indexOf("#")) : href;
    }

}

