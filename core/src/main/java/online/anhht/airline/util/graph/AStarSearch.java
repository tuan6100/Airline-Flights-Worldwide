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
 * A* (A-Star) search algorithm for finding optimal shortest paths with an admissible heuristic.
 * Implements {@link ShortestPathFinder}.
 */
public class AStarSearch<V> implements ShortestPathFinder<V> {

    private final Heuristic<V> heuristic;

    public AStarSearch(@NonNull Heuristic<V> heuristic) {
        this.heuristic = Objects.requireNonNull(heuristic, "Heuristic must not be null");
    }

    public AStarSearch() {
        this(Heuristic.zero());
    }

    private record NodeScore<V>(V node, double fScore) {}

    @Override
    public WeightedPath<V> findShortestPath(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        Objects.requireNonNull(graph, "Graph must not be null");
        Objects.requireNonNull(start, "Start vertex must not be null");
        Objects.requireNonNull(destination, "Destination vertex must not be null");

        if (start.equals(destination)) {
            return WeightedPath.of(List.of(start), 0.0);
        }

        Map<V, Double> gScore = new HashMap<>();
        Map<V, V> cameFrom = new HashMap<>();
        PriorityQueue<NodeScore<V>> openSet = new PriorityQueue<>(Comparator.comparingDouble(NodeScore::fScore));
        Set<V> closedSet = new HashSet<>();

        gScore.put(start, 0.0);
        double initialF = heuristic.estimate(start, destination);
        openSet.add(new NodeScore<>(start, initialF));

        while (!openSet.isEmpty()) {
            NodeScore<V> current = openSet.poll();
            V u = current.node();

            if (u.equals(destination)) {
                return reconstructPath(cameFrom, u, gScore.get(u));
            }

            if (!closedSet.add(u)) {
                continue;
            }

            double currentG = gScore.getOrDefault(u, Double.POSITIVE_INFINITY);

            for (V neighbor : graph.getNeighbors(u)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double edgeWeight = graph.getEdgeWeight(u, neighbor);
                double tentativeG = currentG + edgeWeight;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    cameFrom.put(neighbor, u);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic.estimate(neighbor, destination);
                    openSet.add(new NodeScore<>(neighbor, f));
                }
            }
        }

        return WeightedPath.empty();
    }

    private WeightedPath<V> reconstructPath(Map<V, V> cameFrom, V current, double totalWeight) {
        List<V> path = new ArrayList<>();
        V step = current;
        while (step != null) {
            path.add(step);
            step = cameFrom.get(step);
        }
        Collections.reverse(path);
        return WeightedPath.of(path, totalWeight);
    }
}
