package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable representation of a weighted path in a network graph.
 */
public record WeightedPath<V>(
        @NonNull List<V> vertices,
        double totalWeight
) {
    public WeightedPath {
        Objects.requireNonNull(vertices, "Vertices list must not be null");
        vertices = List.copyOf(vertices);
    }

    public int hopCount() {
        return Math.max(0, vertices.size() - 1);
    }

    public boolean isEmpty() {
        return vertices.isEmpty();
    }

    public V getOrigin() {
        if (vertices.isEmpty()) return null;
        return vertices.get(0);
    }

    public V getDestination() {
        if (vertices.isEmpty()) return null;
        return vertices.get(vertices.size() - 1);
    }

    public boolean contains(V vertex) {
        return vertices.contains(vertex);
    }

    public static <V> WeightedPath<V> of(List<V> vertices, double totalWeight) {
        return new WeightedPath<>(vertices, totalWeight);
    }

    public static <V> WeightedPath<V> empty() {
        return new WeightedPath<>(Collections.emptyList(), Double.POSITIVE_INFINITY);
    }
}
