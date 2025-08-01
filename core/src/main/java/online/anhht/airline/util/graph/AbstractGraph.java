package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Skeletal implementation of the {@link Graph} interface to minimize the effort required to implement this interface.
 * Inspired by {@code java.util.AbstractCollection} and {@code java.util.AbstractMap}.
 */
public abstract class AbstractGraph<V> implements Graph<V> {

    @Override
    public boolean containsVertex(@NonNull V vertex) {
        Objects.requireNonNull(vertex, "Vertex must not be null");
        return getVertices().contains(vertex);
    }

    @Override
    public int vertexCount() {
        return getVertices().size();
    }

    @Override
    public boolean isEmpty() {
        return getVertices().isEmpty();
    }

    @Override
    public List<V> findPath(@NonNull V start, @NonNull V end) {
        Objects.requireNonNull(start, "Start vertex must not be null");
        Objects.requireNonNull(end, "End vertex must not be null");

        if (!containsVertex(start) || !containsVertex(end)) {
            return List.of();
        }

        // Delegate directly to ShortestPathFinder strategy
        return new DijkstraShortestPath<V>().findPath(this, start, end);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph(vertices=").append(getVertices().size()).append(", edges=[");
        boolean first = true;
        for (V u : getVertices()) {
            for (V v : getNeighbors(u)) {
                if (!first) sb.append(", ");
                sb.append(u).append("->").append(v).append(":").append(getEdgeWeight(u, v));
                first = false;
            }
        }
        sb.append("])");
        return sb.toString();
    }
}
