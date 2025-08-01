package online.anhht.airline.util.graph;

import online.anhht.airline.model.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("A* Search Algorithm Unit Tests")
class AStarSearchTest {

    @Test
    @DisplayName("Should find optimal shortest path using A* search with coordinates heuristic")
    void testAStarWithCoordinateHeuristic() {
        // Grid: SVO (0,0), LED (0,10), KZN (10,0), AER (10,10)
        Map<String, Coordinates> coords = Map.of(
                "SVO", new Coordinates(0.0, 0.0),
                "LED", new Coordinates(0.0, 10.0),
                "KZN", new Coordinates(10.0, 0.0),
                "AER", new Coordinates(10.0, 10.0)
        );

        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 100.0);
        graph.addEdge("LED", "AER", 150.0); // Path 1: SVO -> LED -> AER (total 250)
        graph.addEdge("SVO", "KZN", 80.0);
        graph.addEdge("KZN", "AER", 90.0);  // Path 2: SVO -> KZN -> AER (total 170 - optimal)

        Heuristic<String> euclidean = (u, target) -> {
            Coordinates c1 = coords.get(u);
            Coordinates c2 = coords.get(target);
            if (c1 != null && c2 != null) {
                return c1.distanceTo(c2);
            }
            return 0.0;
        };

        AStarSearch<String> aStar = new AStarSearch<>(euclidean);
        WeightedPath<String> path = aStar.findShortestPath(graph, "SVO", "AER");

        assertFalse(path.isEmpty());
        assertEquals(List.of("SVO", "KZN", "AER"), path.vertices());
        assertEquals(170.0, path.totalWeight(), 0.001);
        assertEquals(2, path.hopCount());
    }

    @Test
    @DisplayName("Should handle unreachable destination gracefully")
    void testUnreachableDestination() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 100.0);
        graph.addVertex("JFK");

        AStarSearch<String> aStar = new AStarSearch<>();
        WeightedPath<String> path = aStar.findShortestPath(graph, "SVO", "JFK");

        assertTrue(path.isEmpty());
    }
}
