package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for computing PageRank-based vertex influence scores in a graph.
 *
 * @param <V> the vertex type
 */
@FunctionalInterface
public interface PageRankAnalyzer<V> {

    /**
     * Computes the PageRank score for all vertices in the graph.
     *
     * @param graph the graph
     * @return unmodifiable map of vertex to PageRank score
     */
    Map<V, Double> calculatePageRank(@NonNull Graph<V> graph);

    /**
     * Returns the top-N vertices ranked by their PageRank score.
     *
     * @param graph the graph
     * @param topN  the number of hub vertices to return
     * @return unmodifiable list of top hub vertices in descending PageRank order
     */
    default List<V> getTopPageRankHubs(@NonNull Graph<V> graph, int topN) {
        return calculatePageRank(graph).entrySet().stream()
                .sorted(Map.Entry.<V, Double>comparingByValue().reversed())
                .limit(Math.max(1, topN))
                .map(Map.Entry::getKey)
                .toList();
    }
}
