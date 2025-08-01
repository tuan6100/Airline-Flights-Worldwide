package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Graph analytics tool for network degree, betweenness centrality (Brandes' algorithm),
 * and airport hub identification.
 * Implements {@link CentralityAnalyzer}.
 */
public class NetworkCentralityAnalyzer<V> implements CentralityAnalyzer<V> {

    private final BreadthFirstSearch<V> bfs;

    public NetworkCentralityAnalyzer(@NonNull BreadthFirstSearch<V> bfs) {
        this.bfs = Objects.requireNonNull(bfs, "BreadthFirstSearch must not be null");
    }

    public NetworkCentralityAnalyzer() {
        this(new BreadthFirstSearch<>());
    }

    @Override
    public Map<V, Integer> calculateDegreeCentrality(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Map<V, Integer> degreeMap = new HashMap<>();

        for (V vertex : graph.getVertices()) {
            degreeMap.put(vertex, 0);
        }

        for (V u : graph.getVertices()) {
            List<V> neighbors = graph.getNeighbors(u);
            degreeMap.put(u, degreeMap.get(u) + neighbors.size());
            for (V v : neighbors) {
                degreeMap.put(v, degreeMap.getOrDefault(v, 0) + 1);
            }
        }

        return Collections.unmodifiableMap(degreeMap);
    }

    @Override
    public Map<V, Double> calculateBetweennessCentrality(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Set<V> vertices = graph.getVertices();
        Map<V, Double> betweenness = new HashMap<>();

        for (V v : vertices) {
            betweenness.put(v, 0.0);
        }

        for (V s : vertices) {
            Stack<V> stack = new Stack<>();
            Map<V, List<V>> predecessors = new HashMap<>();
            Map<V, Integer> sigma = new HashMap<>();
            Map<V, Integer> distance = new HashMap<>();

            for (V w : vertices) {
                predecessors.put(w, new ArrayList<>());
                sigma.put(w, 0);
                distance.put(w, -1);
            }

            sigma.put(s, 1);
            distance.put(s, 0);

            Queue<V> queue = new ArrayDeque<>();
            queue.add(s);

            while (!queue.isEmpty()) {
                V v = queue.poll();
                stack.push(v);

                for (V w : graph.getNeighbors(v)) {
                    // Path discovery
                    if (distance.get(w) < 0) {
                        distance.put(w, distance.get(v) + 1);
                        queue.add(w);
                    }
                    // Path counting
                    if (distance.get(w) == distance.get(v) + 1) {
                        sigma.put(w, sigma.get(w) + sigma.get(v));
                        predecessors.get(w).add(v);
                    }
                }
            }

            Map<V, Double> delta = new HashMap<>();
            for (V v : vertices) {
                delta.put(v, 0.0);
            }

            // Accumulation
            while (!stack.isEmpty()) {
                V w = stack.pop();
                for (V v : predecessors.get(w)) {
                    double c = ((double) sigma.get(v) / (double) sigma.get(w)) * (1.0 + delta.get(w));
                    delta.put(v, delta.get(v) + c);
                }
                if (!w.equals(s)) {
                    betweenness.put(w, betweenness.get(w) + delta.get(w));
                }
            }
        }

        return Collections.unmodifiableMap(betweenness);
    }

    @Override
    public List<V> getTopHubs(@NonNull Graph<V> graph, int topN) {
        Map<V, Double> betweenness = calculateBetweennessCentrality(graph);
        return betweenness.entrySet().stream()
                .sorted(Map.Entry.<V, Double>comparingByValue().reversed())
                .limit(Math.max(1, topN))
                .map(Map.Entry::getKey)
                .toList();
    }

    @Override
    public Set<V> findReachableVertices(@NonNull Graph<V> graph, @NonNull V origin) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(origin, "Origin vertex must not be null");

        // Leverage BFS travel algorithm strategy directly
        List<V> visited = bfs.travel(graph, origin);
        return Collections.unmodifiableSet(new LinkedHashSet<>(visited));
    }

    @Override
    public Map<V, Integer> calculateShortestHopsFrom(@NonNull Graph<V> graph, @NonNull V origin) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(origin, "Origin vertex must not be null");

        // Delegate to BreadthFirstSearch.travelWithHops — single canonical hop-distance BFS
        return bfs.travelWithHops(graph, origin);
    }
}
