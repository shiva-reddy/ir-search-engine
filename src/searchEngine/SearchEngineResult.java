package searchEngine;/* shiva created on 4/27/21 inside the package - PACKAGE_NAME */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchEngineResult {
    List<Map.Entry<String, Double>> documents = new ArrayList<>();

    SearchEngineResult(List<Map.Entry<String, Double>> documents){
        this.documents = documents;
    }

    public void print() {
        System.out.println("Result::");
    }
}
