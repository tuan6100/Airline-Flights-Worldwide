package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Prim's algorithm for computing Minimum Spanning Tree (MST) in network graphs.
 * Optimal for dense graphs with high edge density.
 * Implements {@link SpanningTreeFinder}.
 */
public class PrimMinimumSpanningTree<V> implements SpanningTreeFinder<V> {

    @Override
    public MstResult<V> computeMst(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");

        Set<V> vertices = graph.getVertices();
        if (vertices.isEmpty()) {
            return new MstResult<>(List.of(), 0.0);
        }

        // Build symmetric undirected edge map to treat flight network connections bi-directionally
        Map<V, Map<V, Double>> adj = new HashMap<>();
        for (V u : vertices) {
            for (V v : graph.getNeighbors(u)) {
                double w = graph.getEdgeWeight(u, v);
                adj.computeIfAbsent(u, k -> new HashMap<>()).merge(v, w, Math::min);
                adj.computeIfAbsent(v, k -> new HashMap<>()).merge(u, w, Math::min);
            }
        }

        Set<V> visited = new HashSet<>();
        PriorityQueue<MinimumSpanningTree.Edge<V>> pq = new PriorityQueue<>(Comparator.comparingDouble(MinimumSpanningTree.Edge::weight));
        List<MinimumSpanningTree.Edge<V>> mstEdges = new ArrayList<>();
        double totalWeight = 0.0;

        // Choose starting vertex
        V start = vertices.iterator().next();
        visited.add(start);

        Map<V, Double> startNeighbors = adj.getOrDefault(start, Collections.emptyMap());
        for (Map.Entry<V, Double> entry : startNeighbors.entrySet()) {
            pq.add(new MinimumSpanningTree.Edge<>(start, entry.getKey(), entry.getValue()));
        }

        while (!pq.isEmpty() && visited.size() < vertices.size()) {
            MinimumSpanningTree.Edge<V> edge = pq.poll();
            V u = edge.source();
            V v = edge.destination();

            if (visited.contains(u) && visited.contains(v)) {
                continue;
            }

            V nextVertex = visited.contains(u) ? v : u;
            visited.add(nextVertex);
            mstEdges.add(edge);
            totalWeight += edge.weight();

            Map<V, Double> nextNeighbors = adj.getOrDefault(nextVertex, Collections.emptyMap());
            for (Map.Entry<V, Double> entry : nextNeighbors.entrySet()) {
                V neighbor = entry.getKey();
                if (!visited.contains(neighbor)) {
                    pq.add(new MinimumSpanningTree.Edge<>(nextVertex, neighbor, entry.getValue()));
                }
            }
        }

        return new MstResult<>(Collections.unmodifiableList(mstEdges), totalWeight);
    }
}
