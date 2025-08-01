package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Advanced Graph Implementations and Algorithms Unit Tests")
class AdvancedGraphAlgorithmsTest {

    @Test
    @DisplayName("AdjacencyMatrixGraph should correctly store and query vertices and edges")
    void testAdjacencyMatrixGraph() {
        Graph<String> matrixGraph = new AdjacencyMatrixGraph<>();
        matrixGraph.addEdge("JFK", "LHR", 5500.0);
        matrixGraph.addEdge("JFK", "CDG", 5800.0);
        matrixGraph.addEdge("LHR", "FRA", 650.0);

        assertEquals(4, matrixGraph.vertexCount());
        assertTrue(matrixGraph.hasEdge("JFK", "LHR"));
        assertFalse(matrixGraph.hasEdge("LHR", "JFK"));
        assertEquals(5500.0, matrixGraph.getEdgeWeight("JFK", "LHR"));
        assertEquals(Double.POSITIVE_INFINITY, matrixGraph.getEdgeWeight("FRA", "JFK"));

        List<String> neighbors = matrixGraph.getNeighbors("JFK");
        assertTrue(neighbors.contains("LHR"));
        assertTrue(neighbors.contains("CDG"));
    }

    @Test
    @DisplayName("UndirectedGraph should ensure bidirectional edge symmetry")
    void testUndirectedGraph() {
        Graph<String> undirected = new UndirectedGraph<>();
        undirected.addEdge("SVO", "LED", 650.0);

        assertTrue(undirected.hasEdge("SVO", "LED"));
        assertTrue(undirected.hasEdge("LED", "SVO"));
        assertEquals(650.0, undirected.getEdgeWeight("LED", "SVO"));
    }

    @Test
    @DisplayName("MaskedGraph should hide excluded vertices and edges from queries")
    void testMaskedGraph() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "C", 20.0);
        graph.addEdge("A", "C", 50.0);

        Graph<String> masked = new MaskedGraph<>(graph, Set.of("B"), Map.of("A", Set.of("C")));
        assertFalse(masked.containsVertex("B"));
        assertFalse(masked.hasEdge("A", "B"));
        assertFalse(masked.hasEdge("A", "C"));
        assertTrue(masked.getNeighbors("A").isEmpty());
    }

    @Test
    @DisplayName("BidirectionalDijkstraShortestPath should find the exact shortest path")
    void testBidirectionalDijkstra() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 600.0);
        graph.addEdge("LED", "AER", 1800.0);
        graph.addEdge("SVO", "KZN", 700.0);
        graph.addEdge("KZN", "AER", 1400.0);

        BidirectionalDijkstraShortestPath<String> bidirectional = new BidirectionalDijkstraShortestPath<>();
        WeightedPath<String> path = bidirectional.findShortestPath(graph, "SVO", "AER");

        assertEquals(List.of("SVO", "KZN", "AER"), path.vertices());
        assertEquals(2100.0, path.totalWeight());
    }

    @Test
    @DisplayName("BellmanFordShortestPath should handle negative edge discounts and detect negative cycles")
    void testBellmanFord() {
        Graph<String> graph = new AdjacencyListGraph<>();
        // Discount corridor: A -> B (10) -> C (-4) -> D (15) = 21
        // Direct: A -> D (30)
        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "C", -4.0);
        graph.addEdge("C", "D", 15.0);
        graph.addEdge("A", "D", 30.0);

        BellmanFordShortestPath<String> bellmanFord = new BellmanFordShortestPath<>();
        WeightedPath<String> path = bellmanFord.findShortestPath(graph, "A", "D");

        assertEquals(List.of("A", "B", "C", "D"), path.vertices());
        assertEquals(21.0, path.totalWeight());

        // Negative cycle test
        graph.addEdge("D", "B", -20.0);
        assertThrows(IllegalStateException.class, () -> bellmanFord.findShortestPath(graph, "A", "D"));
    }

    @Test
    @DisplayName("TopologicalSort should compute valid execution order and detect cycles")
    void testTopologicalSort() {
        Graph<String> dag = new AdjacencyListGraph<>();
        dag.addEdge("CheckIn", "Security", 1.0);
        dag.addEdge("Security", "Boarding", 1.0);
        dag.addEdge("BaggageDrop", "Security", 1.0);

        TopologicalSort<String> topo = new TopologicalSort<>();
        List<String> order = topo.sort(dag);

        assertTrue(order.indexOf("CheckIn") < order.indexOf("Security"));
        assertTrue(order.indexOf("BaggageDrop") < order.indexOf("Security"));
        assertTrue(order.indexOf("Security") < order.indexOf("Boarding"));

        // Cycle test
        dag.addEdge("Boarding", "CheckIn", 1.0);
        assertThrows(IllegalStateException.class, () -> topo.sort(dag));
    }

    @Test
    @DisplayName("PrimMinimumSpanningTree should compute correct MST total weight")
    void testPrimMst() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("A", "B", 4.0);
        graph.addEdge("A", "C", 2.0);
        graph.addEdge("B", "C", 1.0);
        graph.addEdge("B", "D", 5.0);
        graph.addEdge("C", "D", 8.0);

        PrimMinimumSpanningTree<String> prim = new PrimMinimumSpanningTree<>();
        MstResult<String> result = prim.computeMst(graph);

        assertEquals(3, result.edges().size());
        assertEquals(8.0, result.totalWeight()); // A-C(2) + C-B(1) + B-D(5)
    }

    @Test
    @DisplayName("PageRankCentralityAnalyzer should assign higher scores to hub nodes")
    void testPageRank() {
        Graph<String> graph = new AdjacencyListGraph<>();
        // SVO is a major hub receiving connections from all regional airports
        graph.addEdge("LED", "SVO", 1.0);
        graph.addEdge("KZN", "SVO", 1.0);
        graph.addEdge("AER", "SVO", 1.0);
        graph.addEdge("OVB", "SVO", 1.0);
        graph.addEdge("SVO", "LED", 1.0);

        PageRankCentralityAnalyzer<String> pageRank = new PageRankCentralityAnalyzer<>();
        Map<String, Double> ranks = pageRank.calculatePageRank(graph);

        assertNotNull(ranks.get("SVO"));
        assertTrue(ranks.get("SVO") > ranks.get("OVB"));

        List<String> topHubs = pageRank.getTopPageRankHubs(graph, 1);
        assertEquals("SVO", topHubs.get(0));
    }
}
