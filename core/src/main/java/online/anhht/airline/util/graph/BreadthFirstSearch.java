package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Breadth-First Search traversal algorithm.
 * Provides both ordered-visit traversal and hop-distance labelling.
 */
public final class BreadthFirstSearch<V> extends AbstractTraversal<V> {

    @Override
    public List<V> travel(@NonNull Graph<V> graph, @NonNull V start) {
        validateInput(graph, start);

        if (!graph.getVertices().contains(start)) {
            return List.of();
        }

        List<V> result = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        Queue<V> queue = new ArrayDeque<>();

        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            result.add(current);

            for (V neighbor : graph.getNeighbors(current)) {
                if (visited.add(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * Performs BFS from {@code start} and returns the minimum hop distance from {@code start}
     * to every reachable vertex (including {@code start} itself, which has distance 0).
     *
     * @param graph the graph to traverse
     * @param start the origin vertex
     * @return unmodifiable map of vertex to minimum hop count
     */
    public Map<V, Integer> travelWithHops(@NonNull Graph<V> graph, @NonNull V start) {
        validateInput(graph, start);

        Map<V, Integer> hopsMap = new LinkedHashMap<>();
        Queue<V> queue = new ArrayDeque<>();

        hopsMap.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            V current = queue.poll();
            int currentHop = hopsMap.get(current);

            for (V neighbor : graph.getNeighbors(current)) {
                if (!hopsMap.containsKey(neighbor)) {
                    hopsMap.put(neighbor, currentHop + 1);
                    queue.add(neighbor);
                }
            }
        }

        return Collections.unmodifiableMap(hopsMap);
    }
}
