package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Adjacency list implementation of directed weighted graph.
 * Extends {@link AbstractGraph}.
 */
public class AdjacencyListGraph<V> extends AbstractGraph<V> {

    private final Map<V, Map<V, Double>> adjacencyMap = new LinkedHashMap<>();

    @Override
    public synchronized void addVertex(@NonNull V vertex) {
        Objects.requireNonNull(vertex, "Vertex must not be null");
        adjacencyMap.putIfAbsent(vertex, new LinkedHashMap<>());
    }

    @Override
    public synchronized void addEdge(@NonNull V source, @NonNull V destination, double weight) {
        Objects.requireNonNull(source, "Source vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");
        addVertex(source);
        addVertex(destination);
        adjacencyMap.get(source).put(destination, weight);
    }

    @Override
    public synchronized Set<V> getVertices() {
        return Collections.unmodifiableSet(adjacencyMap.keySet());
    }

    @Override
    public synchronized List<V> getNeighbors(@NonNull V vertex) {
        Map<V, Double> neighbors = adjacencyMap.get(vertex);
        if (neighbors == null) {
            return List.of();
        }
        return new ArrayList<>(neighbors.keySet());
    }

    @Override
    public synchronized double getEdgeWeight(@NonNull V source, @NonNull V destination) {
        Map<V, Double> neighbors = adjacencyMap.get(source);
        if (neighbors == null || !neighbors.containsKey(destination)) {
            return Double.POSITIVE_INFINITY;
        }
        return neighbors.get(destination);
    }

    @Override
    public synchronized boolean hasEdge(@NonNull V source, @NonNull V destination) {
        Map<V, Double> neighbors = adjacencyMap.get(source);
        return neighbors != null && neighbors.containsKey(destination);
    }
}
