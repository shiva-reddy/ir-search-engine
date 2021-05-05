/* shiva created on 5/5/21 inside the package - searchEngine */
package searchEngine;

public class CosineSimilarityCombinationStrategy implements MetricCombinationStrategy{
    @Override
    public Double combine(Double m1, Double m2) {
        if(m1 == null) m1 = 0.0d;
        if(m2 == null) m2 = 0.0d;
        return m2*0.8 + m1*0.2;
    }
}
