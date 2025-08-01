package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for analyzing network graph centrality, connectivity, and hub structures.
 */
public interface CentralityAnalyzer<V> {

    /**
     * Calculates degree centrality (total incident edges) for each vertex in the graph.
     */
    Map<V, Integer> calculateDegreeCentrality(@NonNull Graph<V> graph);

    /**
     * Calculates Betweenness Centrality for each vertex using Brandes' algorithm.
     */
    Map<V, Double> calculateBetweennessCentrality(@NonNull Graph<V> graph);

    /**
     * Identifies top hub vertices ranked by betweenness centrality score.
     */
    List<V> getTopHubs(@NonNull Graph<V> graph, int topN);

    /**
     * Finds all vertices reachable from the specified origin vertex.
     */
    Set<V> findReachableVertices(@NonNull Graph<V> graph, @NonNull V origin);

    /**
     * Calculates shortest hop distances (fewest edge hops) from an origin vertex to all reachable vertices.
     */
    Map<V, Integer> calculateShortestHopsFrom(@NonNull Graph<V> graph, @NonNull V origin);
}
