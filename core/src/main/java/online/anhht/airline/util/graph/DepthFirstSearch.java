package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Depth-First Search traversal algorithm.
 */
public final class DepthFirstSearch<V> extends AbstractTraversal<V> {

    @Override
    public List<V> travel(@NonNull Graph<V> graph, @NonNull V start) {
        validateInput(graph, start);

        if (!graph.getVertices().contains(start)) {
            return List.of();
        }

        List<V> result = new ArrayList<>();
        Set<V> visited = new HashSet<>();
        Deque<V> stack = new ArrayDeque<>();

        stack.push(start);

        while (!stack.isEmpty()) {
            V current = stack.pop();
            if (visited.add(current)) {
                result.add(current);

                List<V> neighbors = graph.getNeighbors(current);
                for (int i = neighbors.size() - 1; i >= 0; i--) {
                    V neighbor = neighbors.get(i);
                    if (!visited.contains(neighbor)) {
                        stack.push(neighbor);
                    }
                }
            }
        }

        return Collections.unmodifiableList(result);
    }
}
