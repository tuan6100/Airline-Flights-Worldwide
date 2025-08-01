package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * PageRank link-analysis algorithm for evaluating node influence and hub importance across the flight network.
 * Implements {@link PageRankAnalyzer}.
 */
public class PageRankCentralityAnalyzer<V> implements PageRankAnalyzer<V> {

    private final double dampingFactor;
    private final int maxIterations;
    private final double tolerance;

    public PageRankCentralityAnalyzer(double dampingFactor, int maxIterations, double tolerance) {
        this.dampingFactor = dampingFactor;
        this.maxIterations = maxIterations;
        this.tolerance = tolerance;
    }

    public PageRankCentralityAnalyzer() {
        this(0.85, 100, 1e-6);
    }

    /**
     * Computes the PageRank score distribution for all vertices in the graph.
     *
     * @param graph the graph
     * @return unmodifiable map of vertex to PageRank score
     */
    public Map<V, Double> calculatePageRank(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");

        Set<V> vertices = graph.getVertices();
        int n = vertices.size();
        if (n == 0) {
            return Collections.emptyMap();
        }

        Map<V, Double> rank = new HashMap<>();
        double initialRank = 1.0 / n;
        for (V v : vertices) {
            rank.put(v, initialRank);
        }

        // Precompute out-degrees and incoming neighbors
        Map<V, Integer> outDegree = new HashMap<>();
        Map<V, List<V>> incoming = new HashMap<>();
        for (V v : vertices) {
            incoming.put(v, new ArrayList<>());
        }

        for (V u : vertices) {
            List<V> neighbors = graph.getNeighbors(u);
            outDegree.put(u, neighbors.size());
            for (V v : neighbors) {
                incoming.get(v).add(u);
            }
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            Map<V, Double> nextRank = new HashMap<>();
            double danglingSum = 0.0;

            for (V u : vertices) {
                if (outDegree.get(u) == 0) {
                    danglingSum += rank.get(u);
                }
            }

            double diff = 0.0;
            for (V v : vertices) {
                double inSum = 0.0;
                for (V u : incoming.get(v)) {
                    inSum += rank.get(u) / outDegree.get(u);
                }

                double newRank = ((1.0 - dampingFactor) / n)
                        + dampingFactor * (inSum + danglingSum / n);

                nextRank.put(v, newRank);
                diff += Math.abs(newRank - rank.get(v));
            }

            rank = nextRank;
            if (diff < tolerance) {
                break;
            }
        }

        return Collections.unmodifiableMap(rank);
    }

    /**
     * Identifies top hub vertices ranked by PageRank score.
     * Delegates to {@link PageRankAnalyzer#getTopPageRankHubs} default implementation.
     */
    @Override
    public List<V> getTopPageRankHubs(@NonNull Graph<V> graph, int topN) {
        return PageRankAnalyzer.super.getTopPageRankHubs(graph, topN);
    }
}
