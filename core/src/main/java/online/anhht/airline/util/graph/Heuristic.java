package online.anhht.airline.util.graph;

/**
 * Heuristic function interface for estimating the distance/cost between two graph vertices.
 */
@FunctionalInterface
public interface Heuristic<V> {

    /**
     * Estimates remaining cost/distance from current vertex to destination vertex.
     * Must be admissible (never overestimate true cost) to guarantee optimal A* search.
     */
    double estimate(V current, V destination);

    /**
     * Zero heuristic (equivalent to standard Dijkstra shortest path).
     */
    static <V> Heuristic<V> zero() {
        return (c, d) -> 0.0;
    }
}
