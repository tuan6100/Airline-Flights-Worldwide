package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Functional interface for finding all simple paths within a maximum allowable number of hops.
 */
@FunctionalInterface
public interface BoundedPathFinder<V> {

    /**
     * Discovers all paths from start to destination that do not exceed the specified maximum hops.
     *
     * @param graph       the graph
     * @param start       the starting vertex
     * @param destination the destination vertex
     * @param maxHops     maximum allowable edge traversals (transfers/stops + 1)
     * @return unmodifiable list of weighted paths sorted by total weight
     */
    List<WeightedPath<V>> findAllPaths(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination, int maxHops);
}
