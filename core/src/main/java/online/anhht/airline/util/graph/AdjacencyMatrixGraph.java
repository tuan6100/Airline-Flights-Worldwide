package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Adjacency matrix implementation of directed weighted graph.
 * Offers $O(1)$ edge existence and edge weight lookup times, optimal for dense networks.
 * Extends {@link AbstractGraph}.
 */
public class AdjacencyMatrixGraph<V> extends AbstractGraph<V> {

    private static final int INITIAL_CAPACITY = 16;
    private final Map<V, Integer> vertexToIndex = new HashMap<>();
    private final List<V> indexToVertex = new ArrayList<>();
    private double[][] matrix;
    private int size = 0;

    public AdjacencyMatrixGraph(int initialCapacity) {
        int cap = Math.max(initialCapacity, INITIAL_CAPACITY);
        this.matrix = new double[cap][cap];
        for (double[] row : matrix) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
    }

    public AdjacencyMatrixGraph() {
        this(INITIAL_CAPACITY);
    }

    @Override
    public synchronized void addVertex(@NonNull V vertex) {
        Objects.requireNonNull(vertex, "Vertex must not be null");
        if (!vertexToIndex.containsKey(vertex)) {
            ensureCapacity(size + 1);
            int idx = size++;
            vertexToIndex.put(vertex, idx);
            indexToVertex.add(vertex);
        }
    }

    @Override
    public synchronized void addEdge(@NonNull V source, @NonNull V destination, double weight) {
        Objects.requireNonNull(source, "Source vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");
        addVertex(source);
        addVertex(destination);

        int srcIdx = vertexToIndex.get(source);
        int destIdx = vertexToIndex.get(destination);
        matrix[srcIdx][destIdx] = weight;
    }

    @Override
    public synchronized Set<V> getVertices() {
        return Collections.unmodifiableSet(vertexToIndex.keySet());
    }

    @Override
    public synchronized List<V> getNeighbors(@NonNull V vertex) {
        Objects.requireNonNull(vertex, "Vertex must not be null");
        Integer srcIdx = vertexToIndex.get(vertex);
        if (srcIdx == null) {
            return List.of();
        }

        List<V> neighbors = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            if (!Double.isInfinite(matrix[srcIdx][j])) {
                neighbors.add(indexToVertex.get(j));
            }
        }
        return Collections.unmodifiableList(neighbors);
    }

    @Override
    public synchronized double getEdgeWeight(@NonNull V source, @NonNull V destination) {
        Objects.requireNonNull(source, "Source vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        Integer srcIdx = vertexToIndex.get(source);
        Integer destIdx = vertexToIndex.get(destination);
        if (srcIdx == null || destIdx == null) {
            return Double.POSITIVE_INFINITY;
        }

        return matrix[srcIdx][destIdx];
    }

    @Override
    public synchronized boolean hasEdge(@NonNull V source, @NonNull V destination) {
        return !Double.isInfinite(getEdgeWeight(source, destination));
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > matrix.length) {
            int newCap = Math.max(matrix.length * 2, minCapacity);
            double[][] newMatrix = new double[newCap][newCap];
            for (int i = 0; i < newCap; i++) {
                Arrays.fill(newMatrix[i], Double.POSITIVE_INFINITY);
            }
            for (int i = 0; i < size; i++) {
                System.arraycopy(matrix[i], 0, newMatrix[i], 0, size);
            }
            this.matrix = newMatrix;
        }
    }
}
