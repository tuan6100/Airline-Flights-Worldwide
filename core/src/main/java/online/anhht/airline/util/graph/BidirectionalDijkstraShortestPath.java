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
 * Bidirectional Dijkstra shortest path algorithm.
 * Runs simultaneous forward and backward searches from origin and destination,
 * significantly pruning the search space on large flight networks.
 * Implements {@link ShortestPathFinder}.
 */
public class BidirectionalDijkstraShortestPath<V> implements ShortestPathFinder<V> {

    private record NodeDistance<V>(V node, double dist) {}

    @Override
    public WeightedPath<V> findShortestPath(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(start, "Start vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        if (!graph.containsVertex(start) || !graph.containsVertex(destination)) {
            return WeightedPath.empty();
        }

        if (start.equals(destination)) {
            return WeightedPath.of(List.of(start), 0.0);
        }

        // Build reverse adjacency for backward search on directed graph
        Map<V, List<V>> reverseAdj = new HashMap<>();
        for (V u : graph.getVertices()) {
            for (V v : graph.getNeighbors(u)) {
                reverseAdj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
            }
        }

        Map<V, Double> distF = new HashMap<>();
        Map<V, Double> distB = new HashMap<>();
        Map<V, V> parentF = new HashMap<>();
        Map<V, V> parentB = new HashMap<>();

        PriorityQueue<NodeDistance<V>> pqF = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::dist));
        PriorityQueue<NodeDistance<V>> pqB = new PriorityQueue<>(Comparator.comparingDouble(NodeDistance::dist));

        Set<V> visitedF = new HashSet<>();
        Set<V> visitedB = new HashSet<>();

        distF.put(start, 0.0);
        distB.put(destination, 0.0);
        pqF.add(new NodeDistance<>(start, 0.0));
        pqB.add(new NodeDistance<>(destination, 0.0));

        double bestDistance = Double.POSITIVE_INFINITY;
        V meetingNode = null;

        while (!pqF.isEmpty() && !pqB.isEmpty()) {
            // Forward step
            if (!pqF.isEmpty()) {
                NodeDistance<V> currF = pqF.poll();
                V u = currF.node();
                if (visitedF.add(u)) {
                    if (visitedB.contains(u)) {
                        double total = distF.get(u) + distB.get(u);
                        if (total < bestDistance) {
                            bestDistance = total;
                            meetingNode = u;
                        }
                    }

                    for (V v : graph.getNeighbors(u)) {
                        double weight = graph.getEdgeWeight(u, v);
                        double tentative = distF.get(u) + weight;
                        if (tentative < distF.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                            distF.put(v, tentative);
                            parentF.put(v, u);
                            pqF.add(new NodeDistance<>(v, tentative));

                            if (distB.containsKey(v)) {
                                double candDist = tentative + distB.get(v);
                                if (candDist < bestDistance) {
                                    bestDistance = candDist;
                                    meetingNode = v;
                                }
                            }
                        }
                    }
                }
            }

            // Backward step
            if (!pqB.isEmpty()) {
                NodeDistance<V> currB = pqB.poll();
                V v = currB.node();
                if (visitedB.add(v)) {
                    if (visitedF.contains(v)) {
                        double total = distF.get(v) + distB.get(v);
                        if (total < bestDistance) {
                            bestDistance = total;
                            meetingNode = v;
                        }
                    }

                    List<V> incoming = reverseAdj.getOrDefault(v, Collections.emptyList());
                    for (V u : incoming) {
                        double weight = graph.getEdgeWeight(u, v);
                        double tentative = distB.get(v) + weight;
                        if (tentative < distB.getOrDefault(u, Double.POSITIVE_INFINITY)) {
                            distB.put(u, tentative);
                            parentB.put(u, v);
                            pqB.add(new NodeDistance<>(u, tentative));

                            if (distF.containsKey(u)) {
                                double candDist = distF.get(u) + tentative;
                                if (candDist < bestDistance) {
                                    bestDistance = candDist;
                                    meetingNode = u;
                                }
                            }
                        }
                    }
                }
            }

            // Early termination condition
            double minF = pqF.isEmpty() ? Double.POSITIVE_INFINITY : pqF.peek().dist();
            double minB = pqB.isEmpty() ? Double.POSITIVE_INFINITY : pqB.peek().dist();
            if (minF + minB >= bestDistance) {
                break;
            }
        }

        if (meetingNode == null || Double.isInfinite(bestDistance)) {
            return WeightedPath.empty();
        }

        // Reconstruct path: start -> meetingNode -> destination
        List<V> pathF = new ArrayList<>();
        V curr = meetingNode;
        while (curr != null) {
            pathF.add(curr);
            curr = parentF.get(curr);
        }
        Collections.reverse(pathF);

        List<V> pathB = new ArrayList<>();
        curr = parentB.get(meetingNode);
        while (curr != null) {
            pathB.add(curr);
            curr = parentB.get(curr);
        }

        List<V> fullPath = new ArrayList<>(pathF);
        fullPath.addAll(pathB);

        return WeightedPath.of(fullPath, bestDistance);
    }
}
