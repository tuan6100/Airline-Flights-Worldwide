package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Symmetric undirected graph decorator wrapping any {@link Graph} implementation.
 * Automatically inserts reverse edges for every added edge to maintain bidirectional symmetry.
 */
public class UndirectedGraph<V> extends AbstractGraph<V> {

    private final Graph<V> delegate;

    public UndirectedGraph(@NonNull Graph<V> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate graph must not be null");
    }

    public UndirectedGraph() {
        this(new AdjacencyListGraph<>());
    }

    @Override
    public void addVertex(@NonNull V vertex) {
        delegate.addVertex(vertex);
    }

    @Override
    public void addEdge(@NonNull V source, @NonNull V destination, double weight) {
        delegate.addEdge(source, destination, weight);
        delegate.addEdge(destination, source, weight);
    }

    @Override
    public Set<V> getVertices() {
        return delegate.getVertices();
    }

    @Override
    public List<V> getNeighbors(@NonNull V vertex) {
        return delegate.getNeighbors(vertex);
    }

    @Override
    public double getEdgeWeight(@NonNull V source, @NonNull V destination) {
        return delegate.getEdgeWeight(source, destination);
    }

    @Override
    public boolean hasEdge(@NonNull V source, @NonNull V destination) {
        return delegate.hasEdge(source, destination);
    }
}
