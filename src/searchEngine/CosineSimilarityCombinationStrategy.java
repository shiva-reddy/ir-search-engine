/* shiva created on 5/5/21 inside the package - searchEngine */
package searchEngine;

public class CosineSimilarityCombinationStrategy implements MetricCombinationStrategy{
    @Override
    public Double combine(Double header, Double text) {
        if(header == null) header = 0.0d;
        if(text == null) text = 0.0d;
        return text*0.7 + header*0.3;
    }
}
