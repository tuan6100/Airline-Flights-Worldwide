package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Yen's K-Shortest Paths Algorithm Unit Tests")
class KShortestPathsTest {

    @Test
    @DisplayName("Should find top-K alternative shortest paths in ranked order")
    void testYenKShortestPaths() {
        // Build diamond network with 3 distinct paths from SVO to AER:
        // 1. SVO -> KZN -> AER = 70 + 80 = 150
        // 2. SVO -> LED -> AER = 60 + 100 = 160
        // 3. SVO -> OVB -> AER = 100 + 90 = 190
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("SVO", "KZN", 70.0);
        graph.addEdge("KZN", "AER", 80.0);

        graph.addEdge("SVO", "LED", 60.0);
        graph.addEdge("LED", "AER", 100.0);

        graph.addEdge("SVO", "OVB", 100.0);
        graph.addEdge("OVB", "AER", 90.0);

        KShortestPaths<String> yen = new KShortestPaths<>();
        List<WeightedPath<String>> paths = yen.findKShortestPaths(graph, "SVO", "AER", 3);

        assertEquals(3, paths.size());

        // 1st shortest
        assertEquals(List.of("SVO", "KZN", "AER"), paths.get(0).vertices());
        assertEquals(150.0, paths.get(0).totalWeight(), 0.001);

        // 2nd shortest
        assertEquals(List.of("SVO", "LED", "AER"), paths.get(1).vertices());
        assertEquals(160.0, paths.get(1).totalWeight(), 0.001);

        // 3rd shortest
        assertEquals(List.of("SVO", "OVB", "AER"), paths.get(2).vertices());
        assertEquals(190.0, paths.get(2).totalWeight(), 0.001);
    }
}
