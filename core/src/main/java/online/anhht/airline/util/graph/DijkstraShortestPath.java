package online.anhht.airline.util.graph;

/**
 * Dijkstra's shortest path algorithm for weighted network graphs.
 * Implemented as a zero-heuristic specialization of {@link AStarSearch}.
 */
public class DijkstraShortestPath<V> extends AStarSearch<V> {

    public DijkstraShortestPath() {
        super(Heuristic.zero());
    }
}
