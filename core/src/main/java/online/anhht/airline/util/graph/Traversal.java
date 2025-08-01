package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Strategy interface for graph traversal algorithms (BFS, DFS).
 *
 * @param <V> the vertex type
 */
@FunctionalInterface
public interface Traversal<V> {

    /**
     * Traverses the graph starting from the specified vertex and returns visited vertices in order.
     *
     * @param graph the graph to traverse
     * @param start the origin vertex
     * @return unmodifiable list of vertices in traversal order
     */
    List<V> travel(@NonNull Graph<V> graph, @NonNull V start);

    /**
     * Traverses the graph and applies the consumer action to each visited vertex.
     *
     * @param graph  the graph to traverse
     * @param start  the origin vertex
     * @param action the consumer action for each vertex
     */
    default void travel(@NonNull Graph<V> graph, @NonNull V start, @NonNull Consumer<V> action) {
        Objects.requireNonNull(action, "Action must not be null");
        travel(graph, start).forEach(action);
    }
}
