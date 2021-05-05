/* shiva created on 4/27/21 inside the package - webCrawler */
package webCrawler;

import utilities.Utils;

import java.io.*;
import java.util.*;

public class Resource implements Serializable {

    private String link;

    private Set<String> externalLinks;
    private String allText, headerText, boldText;
    int id;

    public Resource(int id, String link, Set<String> externalLinks, String allText, String headerText, String boldText) {
        this.id = id;
        this.link = link;
        this.externalLinks = externalLinks;
        this.allText = allText;
        this.headerText = headerText;
        this.boldText = boldText;
    }


    public static Resource load(String path) throws IOException, ClassNotFoundException {
        return (Resource) Utils.load(path);
    }

    public Set<String> getExternalLinks() {
        return externalLinks;
    }

    public String getLink(){
        return link;
    }

    public String getText() {
        return allText;
    }

    public String getHeaderText(){
        return headerText;
    }

    public String getBoldText(){
        return boldText;
    }
}
