package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Dynamic subgraph view of an underlying {@link Graph} with specific vertices and directed edges masked out.
 * Inspired by subview design patterns in the Java Collections Framework (e.g. {@code subList}, {@code subMap}).
 */
public class MaskedGraph<V> extends AbstractGraph<V> {

    private final Graph<V> delegate;
    private final Set<V> maskedVertices;
    private final Map<V, Set<V>> maskedEdges;

    public MaskedGraph(
            @NonNull Graph<V> delegate,
            @NonNull Set<V> maskedVertices,
            @NonNull Map<V, Set<V>> maskedEdges
    ) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate graph must not be null");
        this.maskedVertices = Objects.requireNonNull(maskedVertices, "Masked vertices must not be null");
        this.maskedEdges = Objects.requireNonNull(maskedEdges, "Masked edges must not be null");
    }

    public MaskedGraph(@NonNull Graph<V> delegate, @NonNull Set<V> maskedVertices) {
        this(delegate, maskedVertices, Collections.emptyMap());
    }

    @Override
    public void addVertex(@NonNull V vertex) {
        throw new UnsupportedOperationException("MaskedGraph is a dynamic read-only view");
    }

    @Override
    public void addEdge(@NonNull V source, @NonNull V destination, double weight) {
        throw new UnsupportedOperationException("MaskedGraph is a dynamic read-only view");
    }

    @Override
    public Set<V> getVertices() {
        Set<V> vertices = new HashSet<>(delegate.getVertices());
        vertices.removeAll(maskedVertices);
        return Collections.unmodifiableSet(vertices);
    }

    @Override
    public List<V> getNeighbors(@NonNull V vertex) {
        Objects.requireNonNull(vertex, "Vertex must not be null");
        if (maskedVertices.contains(vertex)) {
            return List.of();
        }

        Set<V> blockedFromU = maskedEdges.getOrDefault(vertex, Collections.emptySet());
        List<V> result = new ArrayList<>();

        for (V neighbor : delegate.getNeighbors(vertex)) {
            if (!maskedVertices.contains(neighbor) && !blockedFromU.contains(neighbor)) {
                result.add(neighbor);
            }
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public double getEdgeWeight(@NonNull V source, @NonNull V destination) {
        Objects.requireNonNull(source, "Source vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        if (maskedVertices.contains(source) || maskedVertices.contains(destination)) {
            return Double.POSITIVE_INFINITY;
        }

        Set<V> blockedFromSource = maskedEdges.get(source);
        if (blockedFromSource != null && blockedFromSource.contains(destination)) {
            return Double.POSITIVE_INFINITY;
        }

        return delegate.getEdgeWeight(source, destination);
    }

    @Override
    public boolean hasEdge(@NonNull V source, @NonNull V destination) {
        return !Double.isInfinite(getEdgeWeight(source, destination)) && delegate.hasEdge(source, destination);
    }
}
