package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

/**
 * Functional interface for calculating Minimum Spanning Trees (MST) in graphs.
 */
@FunctionalInterface
public interface SpanningTreeFinder<V> {

    /**
     * Computes the Minimum Spanning Tree for the given graph.
     *
     * @param graph the graph
     * @return the result containing MST edges and total weight
     */
    MstResult<V> computeMst(@NonNull Graph<V> graph);
}
