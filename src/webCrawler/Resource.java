/* shiva created on 4/27/21 inside the package - webCrawler */
package webCrawler;

import org.jsoup.select.Elements;
import utilities.Utils;

import java.io.*;
import java.util.*;

public class Resource implements Serializable {

    private String link;

    private Set<String> children;
    private String data;
    int id;

    public Resource(int id, String link, Set<String> children, String data) {
        this.id = id;
        this.link = link;
        this.children = children;
        this.data = data;
    }


    public static Resource load(String path) throws IOException, ClassNotFoundException {

        return (Resource) Utils.load(path);
    }

    public Set<String> getChildren() {
        return children;
    }

    public String getLink(){
        return link;
    }

    public String getText() {
        return data;
    }
}
