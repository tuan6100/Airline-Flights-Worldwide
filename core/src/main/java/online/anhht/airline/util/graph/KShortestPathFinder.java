package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Functional interface for finding top-K alternative loopless shortest paths in a graph.
 */
@FunctionalInterface
public interface KShortestPathFinder<V> {

    /**
     * Finds up to K alternative shortest paths between source and destination.
     *
     * @param graph       the graph
     * @param source      the source vertex
     * @param destination the destination vertex
     * @param k           the maximum number of paths to discover
     * @return unmodifiable list of K shortest paths sorted in ascending order of total weight
     */
    List<WeightedPath<V>> findKShortestPaths(@NonNull Graph<V> graph, @NonNull V source, @NonNull V destination, int k);
}
