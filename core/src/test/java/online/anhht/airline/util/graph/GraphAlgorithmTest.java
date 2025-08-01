package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Graph Algorithms and Pathfinding Unit Tests")
class GraphAlgorithmTest {

    @Test
    @DisplayName("Should traverse graph using BFS and DFS correctly")
    void testBfsAndDfsTraversal() {
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 600);
        graph.addEdge("SVO", "KZN", 700);
        graph.addEdge("LED", "AER", 1800);
        graph.addEdge("KZN", "AER", 1500);

        Traversal<String> bfs = new BreadthFirstSearch<>();
        List<String> bfsOrder = bfs.travel(graph, "SVO");
        assertEquals(4, bfsOrder.size());
        assertEquals("SVO", bfsOrder.get(0));

        Traversal<String> dfs = new DepthFirstSearch<>();
        List<String> dfsOrder = dfs.travel(graph, "SVO");
        assertEquals(4, dfsOrder.size());
        assertEquals("SVO", dfsOrder.get(0));
    }

    @Test
    @DisplayName("Should find shortest weighted path using Dijkstra algorithm")
    void testDijkstraShortestPath() {
        AdjacencyListGraph<String> graph = new AdjacencyListGraph<>();
        // SVO -> LED (600) -> AER (1800) = total 2400
        // SVO -> KZN (700) -> AER (1400) = total 2100 (shorter!)
        graph.addEdge("SVO", "LED", 600.0);
        graph.addEdge("LED", "AER", 1800.0);
        graph.addEdge("SVO", "KZN", 700.0);
        graph.addEdge("KZN", "AER", 1400.0);

        ShortestPathFinder<String> dijkstra = new DijkstraShortestPath<>();
        WeightedPath<String> path = dijkstra.findShortestPath(graph, "SVO", "AER");

        assertEquals(List.of("SVO", "KZN", "AER"), path.vertices());
        assertEquals(2100.0, path.totalWeight());
        assertEquals(List.of("SVO", "KZN", "AER"), dijkstra.findPath(graph, "SVO", "AER"));
    }
}
