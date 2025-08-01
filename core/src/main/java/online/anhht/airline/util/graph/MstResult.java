package online.anhht.airline.util.graph;

import java.util.List;

/**
 * Immutable result of a Minimum Spanning Tree computation.
 * Promoted to a top-level type so {@link SpanningTreeFinder} does not leak {@link MinimumSpanningTree}'s inner type.
 *
 * @param edges       the MST edges in order of addition
 * @param totalWeight the sum of all edge weights in the MST
 */
public record MstResult<V>(List<MinimumSpanningTree.Edge<V>> edges, double totalWeight) {}
