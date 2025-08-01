package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Bellman-Ford shortest path algorithm.
 * Supports negative edge weights and detects negative-weight cycles.
 * Implements {@link ShortestPathFinder}.
 */
public class BellmanFordShortestPath<V> implements ShortestPathFinder<V> {

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

        Set<V> vertices = graph.getVertices();
        int numVertices = vertices.size();

        Map<V, Double> dist = new HashMap<>();
        Map<V, V> predecessor = new HashMap<>();

        for (V v : vertices) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(start, 0.0);

        // Relax edges |V| - 1 times
        for (int i = 1; i < numVertices; i++) {
            boolean anyChange = false;
            for (V u : vertices) {
                double distU = dist.get(u);
                if (Double.isInfinite(distU)) {
                    continue;
                }

                for (V v : graph.getNeighbors(u)) {
                    double weight = graph.getEdgeWeight(u, v);
                    if (distU + weight < dist.get(v)) {
                        dist.put(v, distU + weight);
                        predecessor.put(v, u);
                        anyChange = true;
                    }
                }
            }
            if (!anyChange) {
                break;
            }
        }

        // Check for negative-weight cycles
        for (V u : vertices) {
            double distU = dist.get(u);
            if (Double.isInfinite(distU)) {
                continue;
            }

            for (V v : graph.getNeighbors(u)) {
                double weight = graph.getEdgeWeight(u, v);
                if (distU + weight < dist.get(v)) {
                    throw new IllegalStateException("Graph contains a negative-weight cycle reachable from start vertex");
                }
            }
        }

        if (!predecessor.containsKey(destination)) {
            return WeightedPath.empty();
        }

        List<V> path = new ArrayList<>();
        V step = destination;
        while (step != null) {
            path.add(step);
            step = predecessor.get(step);
        }
        Collections.reverse(path);

        return WeightedPath.of(path, dist.get(destination));
    }
}
