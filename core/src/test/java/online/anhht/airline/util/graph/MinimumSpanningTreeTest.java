package online.anhht.airline.util.graph;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Minimum Spanning Tree (MST) Algorithm Unit Tests")
class MinimumSpanningTreeTest {

    @Test
    @DisplayName("Should compute MST with minimum total weight using Kruskal algorithm")
    void testMinimumSpanningTree() {
        // Triangle A-B (10), B-C (15), A-C (30)
        // MST should pick A-B (10) and B-C (15) = total 25
        Graph<String> graph = new AdjacencyListGraph<>();
        graph.addEdge("A", "B", 10.0);
        graph.addEdge("B", "A", 10.0);

        graph.addEdge("B", "C", 15.0);
        graph.addEdge("C", "B", 15.0);

        graph.addEdge("A", "C", 30.0);
        graph.addEdge("C", "A", 30.0);

        MinimumSpanningTree<String> mst = new MinimumSpanningTree<>();
        MstResult<String> result = mst.computeMst(graph);

        assertEquals(2, result.edges().size());
        assertEquals(25.0, result.totalWeight(), 0.001);
    }
}
