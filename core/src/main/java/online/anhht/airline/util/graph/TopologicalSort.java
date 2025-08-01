package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Topological Sort traversal for Directed Acyclic Graphs (DAGs) using Kahn's algorithm.
 * Useful for sequential flight route scheduling, leg dependencies, and maintenance workflows.
 * Implements {@link Traversal}.
 */
public class TopologicalSort<V> extends AbstractTraversal<V> {

    @Override
    public List<V> travel(@NonNull Graph<V> graph, @NonNull V start) {
        validateInput(graph, start);
        return sort(graph);
    }

    /**
     * Performs a full topological sort on the entire graph.
     *
     * @param graph the directed acyclic graph
     * @return unmodifiable list of vertices in topological order
     * @throws IllegalStateException if the graph contains a directed cycle
     */
    public List<V> sort(@NonNull Graph<V> graph) {
        Set<V> vertices = graph.getVertices();
        Map<V, Integer> inDegree = new HashMap<>();

        for (V v : vertices) {
            inDegree.put(v, 0);
        }

        for (V u : vertices) {
            for (V v : graph.getNeighbors(u)) {
                inDegree.put(v, inDegree.getOrDefault(v, 0) + 1);
            }
        }

        Queue<V> zeroInDegreeQueue = new ArrayDeque<>();
        for (Map.Entry<V, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                zeroInDegreeQueue.add(entry.getKey());
            }
        }

        List<V> result = new ArrayList<>();
        while (!zeroInDegreeQueue.isEmpty()) {
            V u = zeroInDegreeQueue.poll();
            result.add(u);

            for (V v : graph.getNeighbors(u)) {
                int newInDegree = inDegree.get(v) - 1;
                inDegree.put(v, newInDegree);
                if (newInDegree == 0) {
                    zeroInDegreeQueue.add(v);
                }
            }
        }

        if (result.size() < vertices.size()) {
            throw new IllegalStateException("Graph contains a cycle; topological sort cannot be computed");
        }

        return Collections.unmodifiableList(result);
    }
}
