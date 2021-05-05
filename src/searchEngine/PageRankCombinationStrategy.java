/* shiva created on 5/5/21 inside the package - searchEngine */
package searchEngine;

public class PageRankCombinationStrategy implements MetricCombinationStrategy{
    @Override
    public Double combine(Double cosine, Double pageRank) {
        return cosine*0.95 + pageRank*0.15;
    }
}
