package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

/**
 * Generic Graph interface for airline route networks and traversals.
 * Inspired by the Java Collections Framework.
 *
 * @param <V> the vertex type
 */
public interface Graph<V> {

    void addVertex(@NonNull V vertex);

    void addEdge(@NonNull V source, @NonNull V destination, double weight);

    default void addEdge(@NonNull V source, @NonNull V destination) {
        addEdge(source, destination, 1.0);
    }

    Set<V> getVertices();

    List<V> getNeighbors(@NonNull V vertex);

    double getEdgeWeight(@NonNull V source, @NonNull V destination);

    boolean hasEdge(@NonNull V source, @NonNull V destination);

    default boolean containsVertex(@NonNull V vertex) {
        return getVertices().contains(vertex);
    }

    default int vertexCount() {
        return getVertices().size();
    }

    default boolean isEmpty() {
        return getVertices().isEmpty();
    }

    List<V> findPath(@NonNull V start, @NonNull V end);

    static <V> Graph<V> of() {
        return new AdjacencyListGraph<>();
    }
}
