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
import java.util.Set;

/**
 * Kruskal's algorithm for computing Minimum Spanning Tree (MST) in network graphs.
 * Implements {@link SpanningTreeFinder}.
 */
public class MinimumSpanningTree<V> implements SpanningTreeFinder<V> {

    public record Edge<V>(V source, V destination, double weight) implements Comparable<Edge<V>> {
        @Override
        public int compareTo(Edge<V> o) {
            return Double.compare(this.weight, o.weight);
        }
    }

    @Override
    public MstResult<V> computeMst(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");

        List<Edge<V>> allEdges = new ArrayList<>();
        Set<String> seenEdges = new HashSet<>();

        for (V u : graph.getVertices()) {
            for (V v : graph.getNeighbors(u)) {
                String edgeKey1 = u.toString() + "->" + v.toString();
                String edgeKey2 = v.toString() + "->" + u.toString();
                if (!seenEdges.contains(edgeKey1) && !seenEdges.contains(edgeKey2)) {
                    double weight = graph.getEdgeWeight(u, v);
                    allEdges.add(new Edge<>(u, v, weight));
                    seenEdges.add(edgeKey1);
                    seenEdges.add(edgeKey2);
                }
            }
        }

        Collections.sort(allEdges);

        UnionFind<V> uf = new UnionFind<>(graph.getVertices());
        List<Edge<V>> mstEdges = new ArrayList<>();
        double totalWeight = 0.0;

        for (Edge<V> edge : allEdges) {
            if (uf.union(edge.source(), edge.destination())) {
                mstEdges.add(edge);
                totalWeight += edge.weight();
            }
        }

        return new MstResult<>(Collections.unmodifiableList(mstEdges), totalWeight);
    }

    private static class UnionFind<V> {
        private final Map<V, V> parent = new HashMap<>();

        UnionFind(Set<V> elements) {
            for (V e : elements) {
                parent.put(e, e);
            }
        }

        V find(V item) {
            V p = parent.get(item);
            if (p == null) return item;
            if (!p.equals(item)) {
                parent.put(item, find(p));
            }
            return parent.get(item);
        }

        boolean union(V x, V y) {
            V rootX = find(x);
            V rootY = find(y);
            if (rootX.equals(rootY)) {
                return false;
            }
            parent.put(rootX, rootY);
            return true;
        }
    }
}
