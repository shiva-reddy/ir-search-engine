/* shiva created on 5/5/21 inside the package - searchEngine */
package searchEngine;

public class CosineSimilarityCombinationStrategy implements MetricCombinationStrategy{
    @Override
    public Double combine(Double m1, Double m2) {
        return m2;
    }
}
