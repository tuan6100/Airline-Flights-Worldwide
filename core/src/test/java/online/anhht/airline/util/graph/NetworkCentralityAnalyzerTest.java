package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Network Centrality and Graph Analytics Unit Tests")
class NetworkCentralityAnalyzerTest {

    @Test
    @DisplayName("Should accurately identify hub nodes by Betweenness Centrality")
    void testBetweennessCentralityAndHubDetection() {
        // Star graph: HUB connected to spokes A, B, C, D
        // All paths between spokes must go through HUB
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("A", "HUB", 1.0);
        graph.addEdge("HUB", "A", 1.0);

        graph.addEdge("B", "HUB", 1.0);
        graph.addEdge("HUB", "B", 1.0);

        graph.addEdge("C", "HUB", 1.0);
        graph.addEdge("HUB", "C", 1.0);

        graph.addEdge("D", "HUB", 1.0);
        graph.addEdge("HUB", "D", 1.0);

        NetworkCentralityAnalyzer<String> analyzer = new NetworkCentralityAnalyzer<>();
        Map<String, Double> betweenness = analyzer.calculateBetweennessCentrality(graph);

        assertTrue(betweenness.get("HUB") > betweenness.get("A"));
        assertTrue(betweenness.get("HUB") > betweenness.get("B"));

        List<String> topHubs = analyzer.getTopHubs(graph, 1);
        assertEquals(List.of("HUB"), topHubs);
    }

    @Test
    @DisplayName("Should find reachable vertices and calculate hop distances")
    void testReachabilityAndHopCount() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "LED", 1.0);
        graph.addEdge("LED", "AER", 1.0);
        graph.addEdge("AER", "KZN", 1.0);
        graph.addVertex("ISOLATED");

        NetworkCentralityAnalyzer<String> analyzer = new NetworkCentralityAnalyzer<>();
        Set<String> reachable = analyzer.findReachableVertices(graph, "SVO");

        assertTrue(reachable.containsAll(Set.of("SVO", "LED", "AER", "KZN")));
        assertFalse(reachable.contains("ISOLATED"));

        Map<String, Integer> hops = analyzer.calculateShortestHopsFrom(graph, "SVO");
        assertEquals(0, hops.get("SVO"));
        assertEquals(1, hops.get("LED"));
        assertEquals(2, hops.get("AER"));
        assertEquals(3, hops.get("KZN"));
    }
}
