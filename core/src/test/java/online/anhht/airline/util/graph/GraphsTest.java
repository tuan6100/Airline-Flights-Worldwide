package online.anhht.airline.util.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Graphs Utility and Interface Redesign Unit Tests")
class GraphsTest {

    private Graph<String> graph;

    @BeforeEach
    void setUp() {
        graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 600.0);
        graph.addEdge("SVO", "KZN", 700.0);
        graph.addEdge("LED", "AER", 1800.0);
        graph.addEdge("KZN", "AER", 1400.0);
    }

    @Test
    @DisplayName("Should find shortest path using Graphs.dijkstra")
    void testGraphsDijkstra() {
        WeightedPath<String> path = Graphs.dijkstra(graph, "SVO", "AER");
        assertEquals(List.of("SVO", "KZN", "AER"), path.vertices());
        assertEquals(2100.0, path.totalWeight());
    }

    @Test
    @DisplayName("Should find shortest path using Graphs.aStar with heuristic")
    void testGraphsAStar() {
        Heuristic<String> heuristic = (u, dest) -> "KZN".equals(u) ? 1400.0 : 2000.0;
        WeightedPath<String> path = Graphs.aStar(graph, "SVO", "AER", heuristic);
        assertEquals(List.of("SVO", "KZN", "AER"), path.vertices());
        assertEquals(2100.0, path.totalWeight());
    }

    @Test
    @DisplayName("Should find k-shortest paths using Graphs.kShortestPaths")
    void testGraphsKShortestPaths() {
        List<WeightedPath<String>> paths = Graphs.kShortestPaths(graph, "SVO", "AER", 2);
        assertEquals(2, paths.size());
        assertEquals(List.of("SVO", "KZN", "AER"), paths.get(0).vertices());
        assertEquals(List.of("SVO", "LED", "AER"), paths.get(1).vertices());
    }

    @Test
    @DisplayName("Should find all paths using Graphs.allPaths")
    void testGraphsAllPaths() {
        List<WeightedPath<String>> paths = Graphs.allPaths(graph, "SVO", "AER", 2);
        assertEquals(2, paths.size());
    }

    @Test
    @DisplayName("Should traverse using Graphs.bfs and Graphs.dfs")
    void testGraphsTraversals() {
        List<String> bfsOrder = Graphs.bfs(graph, "SVO");
        assertEquals(4, bfsOrder.size());
        assertEquals("SVO", bfsOrder.get(0));

        List<String> dfsOrder = Graphs.dfs(graph, "SVO");
        assertEquals(4, dfsOrder.size());
        assertEquals("SVO", dfsOrder.get(0));
    }

    @Test
    @DisplayName("Should compute centralities and reachability using Graphs")
    void testGraphsAnalytics() {
        Map<String, Integer> degrees = Graphs.degreeCentrality(graph);
        assertTrue(degrees.get("SVO") >= 2);

        Set<String> reachable = Graphs.reachableVertices(graph, "SVO");
        assertEquals(Set.of("SVO", "LED", "KZN", "AER"), reachable);

        Map<String, Integer> hops = Graphs.shortestHops(graph, "SVO");
        assertEquals(0, hops.get("SVO"));
        assertEquals(1, hops.get("LED"));
        assertEquals(2, hops.get("AER"));

        List<String> hubs = Graphs.topHubs(graph, 2);
        assertFalse(hubs.isEmpty());
    }

    @Test
    @DisplayName("Should calculate Minimum Spanning Tree using Graphs.minimumSpanningTree")
    void testGraphsMst() {
        MstResult<String> mst = Graphs.minimumSpanningTree(graph);
        assertEquals(3, mst.edges().size());
        assertEquals(2700.0, mst.totalWeight());
    }

    @Test
    @DisplayName("Should enforce immutability on Graphs.unmodifiableGraph and Graphs.emptyGraph")
    void testUnmodifiableAndEmptyGraphs() {
        Graph<String> unmodifiable = Graphs.unmodifiableGraph(graph);
        assertEquals(4, unmodifiable.getVertices().size());
        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.addVertex("OVB"));
        assertThrows(UnsupportedOperationException.class, () -> unmodifiable.addEdge("SVO", "OVB", 3000));

        Graph<String> empty = Graphs.emptyGraph();
        assertTrue(empty.isEmpty());
        assertEquals(0, empty.vertexCount());
        assertThrows(UnsupportedOperationException.class, () -> empty.addVertex("SVO"));
    }

    @Test
    @DisplayName("AbstractGraph default methods should operate correctly")
    void testAbstractGraphDefaults() {
        assertTrue(graph.containsVertex("SVO"));
        assertFalse(graph.containsVertex("JFK"));
        assertEquals(4, graph.vertexCount());
        assertFalse(graph.isEmpty());
        List<String> path = graph.findPath("SVO", "AER");
        assertFalse(path.isEmpty());
    }
}
