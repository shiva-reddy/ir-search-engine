/* shiva created on 4/27/21 inside the package - webCrawler */
package webCrawler;

import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Resource implements Serializable {

    private String link;

    private List<String> children = new ArrayList<>();
    private String data;
    int id;

    public Resource(int id, String link, List<String> children, String data) {
        this.id = id;
        this.link = link;
        this.children = children;
        this.data = data;
    }


    public List<String> getChildren() {
        return children;
    }

    public String getLink(){
        return link;
    }

    public String getText() {
        return data;
    }
}
