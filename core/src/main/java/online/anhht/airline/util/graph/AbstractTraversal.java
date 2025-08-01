package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Skeletal base class for graph traversal algorithms providing common validation.
 */
public abstract class AbstractTraversal<V> implements Traversal<V> {

    protected void validateInput(@NonNull Graph<V> graph, @NonNull V start) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(start, "Start vertex must not be null");
    }
}
