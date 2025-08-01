package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("All Paths Finder Algorithm Unit Tests")
class AllPathsFinderTest {

    @Test
    @DisplayName("Should find all paths within max stops/hops limit")
    void testFindAllPathsWithinMaxHops() {
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "D", 20.0); // A -> B -> D (2 hops)

        graph.addEdge("A", "C", 15.0);
        graph.addEdge("C", "E", 10.0);
        graph.addEdge("E", "D", 10.0); // A -> C -> E -> D (3 hops)

        graph.addEdge("A", "D", 50.0); // A -> D (1 hop - direct)

        AllPathsFinder<String> finder = new AllPathsFinder<>();

        // Max hops = 2 should find Direct (1 hop) and A->B->D (2 hops), excluding 3-hop path
        List<WeightedPath<String>> paths2Hops = finder.findAllPaths(graph, "A", "D", 2);
        assertEquals(2, paths2Hops.size());
        assertEquals(List.of("A", "B", "D"), paths2Hops.get(0).vertices()); // weight 30
        assertEquals(List.of("A", "D"), paths2Hops.get(1).vertices());      // weight 50

        // Max hops = 3 should find all 3 paths
        List<WeightedPath<String>> paths3Hops = finder.findAllPaths(graph, "A", "D", 3);
        assertEquals(3, paths3Hops.size());
    }
}
