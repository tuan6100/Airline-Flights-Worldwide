package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Functional interface for finding the shortest weighted path between two vertices in a graph.
 * Analogous to Java Collections Framework strategy interfaces.
 */
@FunctionalInterface
public interface ShortestPathFinder<V> {

    /**
     * Finds the shortest weighted path from start to destination in the specified graph.
     *
     * @param graph       the graph
     * @param start       the origin vertex
     * @param destination the target vertex
     * @return the weighted shortest path, or {@link WeightedPath#empty()} if unreachable
     */
    WeightedPath<V> findShortestPath(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination);

    /**
     * Convenience method returning the sequence of vertices in the shortest path.
     *
     * @param graph       the graph
     * @param start       the origin vertex
     * @param destination the target vertex
     * @return unmodifiable list of vertices along the shortest path
     */
    default List<V> findPath(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        return findShortestPath(graph, start, destination).vertices();
    }
}
