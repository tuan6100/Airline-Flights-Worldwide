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
 * Yen's algorithm for computing top-K loopless shortest paths in a directed weighted graph.
 * Uses {@link MaskedGraph} and {@link ShortestPathFinder} for modular, reusable search execution.
 * Implements {@link KShortestPathFinder}.
 */
public class KShortestPaths<V> implements KShortestPathFinder<V> {

    private final ShortestPathFinder<V> shortestPathFinder;

    public KShortestPaths(@NonNull ShortestPathFinder<V> shortestPathFinder) {
        this.shortestPathFinder = Objects.requireNonNull(shortestPathFinder, "ShortestPathFinder must not be null");
    }

    public KShortestPaths() {
        this(new DijkstraShortestPath<>());
    }

    @Override
    public List<WeightedPath<V>> findKShortestPaths(@NonNull Graph<V> graph, @NonNull V source, @NonNull V destination, int k) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(source, "Source vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        if (k <= 0) {
            return List.of();
        }

        List<WeightedPath<V>> determinedPaths = new ArrayList<>();

        // 1. Find initial shortest path using injected ShortestPathFinder
        WeightedPath<V> firstPath = shortestPathFinder.findShortestPath(graph, source, destination);
        if (firstPath.isEmpty()) {
            return List.of();
        }

        determinedPaths.add(firstPath);

        PriorityQueue<WeightedPath<V>> candidateQueue = new PriorityQueue<>(Comparator.comparingDouble(WeightedPath::totalWeight));
        Set<List<V>> candidateSet = new HashSet<>();

        // 2. Iteratively discover k-th shortest path
        for (int kIndex = 1; kIndex < k; kIndex++) {
            List<V> previousPath = determinedPaths.get(kIndex - 1).vertices();

            for (int i = 0; i < previousPath.size() - 1; i++) {
                V spurNode = previousPath.get(i);
                List<V> rootPath = previousPath.subList(0, i + 1);

                Map<V, Set<V>> removedEdges = new HashMap<>();
                Set<V> removedNodes = new HashSet<>();

                for (WeightedPath<V> p : determinedPaths) {
                    List<V> pVerts = p.vertices();
                    if (pVerts.size() > i && pVerts.subList(0, i + 1).equals(rootPath)) {
                        V u = pVerts.get(i);
                        V v = pVerts.get(i + 1);
                        removedEdges.computeIfAbsent(u, key -> new HashSet<>()).add(v);
                    }
                }

                for (int r = 0; r < i; r++) {
                    removedNodes.add(rootPath.get(r));
                }

                // Leverage MaskedGraph view and the existing ShortestPathFinder
                Graph<V> maskedGraph = new MaskedGraph<>(graph, removedNodes, removedEdges);
                WeightedPath<V> spurPath = shortestPathFinder.findShortestPath(maskedGraph, spurNode, destination);

                if (!spurPath.isEmpty()) {
                    List<V> totalPath = new ArrayList<>(rootPath.subList(0, rootPath.size() - 1));
                    totalPath.addAll(spurPath.vertices());

                    if (!candidateSet.contains(totalPath) && !containsPath(determinedPaths, totalPath)) {
                        double totalWeight = calculatePathWeight(graph, totalPath);
                        WeightedPath<V> candidate = WeightedPath.of(totalPath, totalWeight);
                        candidateQueue.add(candidate);
                        candidateSet.add(totalPath);
                    }
                }
            }

            if (candidateQueue.isEmpty()) {
                break;
            }

            WeightedPath<V> nextShortest = candidateQueue.poll();
            determinedPaths.add(nextShortest);
        }

        return Collections.unmodifiableList(determinedPaths);
    }

    private boolean containsPath(List<WeightedPath<V>> paths, List<V> target) {
        for (WeightedPath<V> p : paths) {
            if (p.vertices().equals(target)) return true;
        }
        return false;
    }

    private double calculatePathWeight(Graph<V> graph, List<V> path) {
        double weight = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            weight += graph.getEdgeWeight(path.get(i), path.get(i + 1));
        }
        return weight;
    }
}
