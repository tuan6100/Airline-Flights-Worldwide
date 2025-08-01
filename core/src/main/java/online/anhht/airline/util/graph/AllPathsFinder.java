package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Discovers all simple paths between two vertices within a specified maximum number of hops/stops.
 * Implements {@link BoundedPathFinder}.
 */
public class AllPathsFinder<V> implements BoundedPathFinder<V> {

    @Override
    public List<WeightedPath<V>> findAllPaths(
            @NonNull Graph<V> graph,
            @NonNull V start,
            @NonNull V destination,
            int maxHops
    ) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(start, "Start vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        if (maxHops < 0) {
            return List.of();
        }

        List<WeightedPath<V>> result = new ArrayList<>();
        List<V> currentPath = new ArrayList<>();
        Set<V> visited = new HashSet<>();

        currentPath.add(start);
        visited.add(start);

        dfs(graph, start, destination, maxHops, 0.0, currentPath, visited, result);

        result.sort(Comparator.comparingDouble(WeightedPath::totalWeight));
        return Collections.unmodifiableList(result);
    }

    private void dfs(
            Graph<V> graph,
            V current,
            V destination,
            int maxHops,
            double currentWeight,
            List<V> currentPath,
            Set<V> visited,
            List<WeightedPath<V>> result
    ) {
        if (current.equals(destination)) {
            result.add(WeightedPath.of(new ArrayList<>(currentPath), currentWeight));
            return;
        }

        if (currentPath.size() - 1 >= maxHops) {
            return;
        }

        for (V neighbor : graph.getNeighbors(current)) {
            if (!visited.contains(neighbor)) {
                double edgeWeight = graph.getEdgeWeight(current, neighbor);
                visited.add(neighbor);
                currentPath.add(neighbor);

                dfs(graph, neighbor, destination, maxHops, currentWeight + edgeWeight, currentPath, visited, result);

                currentPath.remove(currentPath.size() - 1);
                visited.remove(neighbor);
            }
        }
    }
}
