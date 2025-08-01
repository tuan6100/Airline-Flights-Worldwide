package online.anhht.airline.util.graph;

import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Static utility methods for operating on and creating {@link Graph} instances and running graph algorithms.
 * Inspired by {@code java.util.Collections}.
 */
public final class Graphs {

    private static final CentralityAnalyzer<?> CENTRALITY_ANALYZER = new NetworkCentralityAnalyzer<>();
    private static final SpanningTreeFinder<?> KRUSKAL_MST_FINDER = new MinimumSpanningTree<>();
    private static final SpanningTreeFinder<?> PRIM_MST_FINDER = new PrimMinimumSpanningTree<>();
    private static final KShortestPathFinder<?> K_SHORTEST_PATH_FINDER = new KShortestPaths<>();
    private static final BoundedPathFinder<?> BOUNDED_PATH_FINDER = new AllPathsFinder<>();
    private static final Traversal<?> BFS_TRAVERSAL = new BreadthFirstSearch<>();
    private static final Traversal<?> DFS_TRAVERSAL = new DepthFirstSearch<>();
    private static final TopologicalSort<?> TOPOLOGICAL_SORT = new TopologicalSort<>();
    private static final PageRankAnalyzer<?> PAGERANK_ANALYZER = new PageRankCentralityAnalyzer<>();

    private Graphs() {
        // Prevent instantiation
    }

    /**
     * Returns an unmodifiable view of the specified graph.
     * Query operations read through to the underlying graph, but modification operations throw {@link UnsupportedOperationException}.
     */
    public static <V> Graph<V> unmodifiableGraph(@NonNull Graph<V> graph) {
        Objects.requireNonNull(graph, "Graph must not be null");
        return new UnmodifiableGraph<>(graph);
    }

    /**
     * Returns an empty, immutable graph.
     */
    @SuppressWarnings("unchecked")
    public static <V> Graph<V> emptyGraph() {
        return (Graph<V>) EmptyGraph.INSTANCE;
    }

    /**
     * Returns a dynamic masked subgraph view of the specified graph.
     */
    public static <V> Graph<V> maskedGraph(@NonNull Graph<V> graph, @NonNull Set<V> maskedVertices, @NonNull Map<V, Set<V>> maskedEdges) {
        return new MaskedGraph<>(graph, maskedVertices, maskedEdges);
    }

    /**
     * Returns an undirected symmetric decorator wrapping the specified graph.
     */
    public static <V> Graph<V> undirectedGraph(@NonNull Graph<V> graph) {
        return new UndirectedGraph<>(graph);
    }

    /**
     * Creates a new matrix-backed directed weighted graph.
     */
    public static <V> Graph<V> matrixGraph(int initialCapacity) {
        return new AdjacencyMatrixGraph<>(initialCapacity);
    }

    /**
     * Finds the shortest weighted path using Dijkstra's algorithm.
     */
    public static <V> WeightedPath<V> dijkstra(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        return new DijkstraShortestPath<V>().findShortestPath(graph, start, destination);
    }

    /**
     * Finds the shortest weighted path using Bidirectional Dijkstra algorithm.
     */
    public static <V> WeightedPath<V> bidirectionalDijkstra(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        return new BidirectionalDijkstraShortestPath<V>().findShortestPath(graph, start, destination);
    }

    /**
     * Finds the shortest weighted path using Bellman-Ford algorithm with negative edge support.
     */
    public static <V> WeightedPath<V> bellmanFord(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination) {
        return new BellmanFordShortestPath<V>().findShortestPath(graph, start, destination);
    }

    /**
     * Finds the shortest weighted path using A* search with an admissible heuristic.
     */
    public static <V> WeightedPath<V> aStar(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination, @NonNull Heuristic<V> heuristic) {
        return new AStarSearch<V>(heuristic).findShortestPath(graph, start, destination);
    }

    /**
     * Finds the top-K loopless alternative shortest paths using Yen's algorithm.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<WeightedPath<V>> kShortestPaths(@NonNull Graph<V> graph, @NonNull V source, @NonNull V destination, int k) {
        return ((KShortestPathFinder<V>) K_SHORTEST_PATH_FINDER).findKShortestPaths(graph, source, destination, k);
    }

    /**
     * Discovers all paths within maximum allowable hops using bounded depth-first search.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<WeightedPath<V>> allPaths(@NonNull Graph<V> graph, @NonNull V start, @NonNull V destination, int maxHops) {
        return ((BoundedPathFinder<V>) BOUNDED_PATH_FINDER).findAllPaths(graph, start, destination, maxHops);
    }

    /**
     * Traverses the graph in Breadth-First Search (BFS) order from the starting vertex.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> bfs(@NonNull Graph<V> graph, @NonNull V start) {
        return ((Traversal<V>) BFS_TRAVERSAL).travel(graph, start);
    }

    /**
     * Traverses the graph in Depth-First Search (DFS) order from the starting vertex.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> dfs(@NonNull Graph<V> graph, @NonNull V start) {
        return ((Traversal<V>) DFS_TRAVERSAL).travel(graph, start);
    }

    /**
     * Performs BFS from {@code start} and returns the minimum hop distance to every reachable vertex.
     * Equivalent to unweighted shortest-path distance in hop count.
     */
    public static <V> Map<V, Integer> bfsWithHops(@NonNull Graph<V> graph, @NonNull V start) {
        return new BreadthFirstSearch<V>().travelWithHops(graph, start);
    }

    /**
     * Computes the topological sort order of a Directed Acyclic Graph (DAG).
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> topologicalSort(@NonNull Graph<V> graph) {
        return ((TopologicalSort<V>) TOPOLOGICAL_SORT).sort(graph);
    }

    /**
     * Computes Brandes' Betweenness Centrality for each vertex.
     */
    @SuppressWarnings("unchecked")
    public static <V> Map<V, Double> betweennessCentrality(@NonNull Graph<V> graph) {
        return ((CentralityAnalyzer<V>) CENTRALITY_ANALYZER).calculateBetweennessCentrality(graph);
    }

    /**
     * Computes degree centrality (incident edges) for each vertex.
     */
    @SuppressWarnings("unchecked")
    public static <V> Map<V, Integer> degreeCentrality(@NonNull Graph<V> graph) {
        return ((CentralityAnalyzer<V>) CENTRALITY_ANALYZER).calculateDegreeCentrality(graph);
    }

    /**
     * Computes PageRank scores for all vertices in the graph.
     */
    @SuppressWarnings("unchecked")
    public static <V> Map<V, Double> pageRank(@NonNull Graph<V> graph) {
        return ((PageRankAnalyzer<V>) PAGERANK_ANALYZER).calculatePageRank(graph);
    }

    /**
     * Identifies top hub vertices ranked by PageRank scores.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> topPageRankHubs(@NonNull Graph<V> graph, int topN) {
        return ((PageRankAnalyzer<V>) PAGERANK_ANALYZER).getTopPageRankHubs(graph, topN);
    }

    /**
     * Identifies top hub vertices ranked by betweenness centrality score.
     */
    @SuppressWarnings("unchecked")
    public static <V> List<V> topHubs(@NonNull Graph<V> graph, int topN) {
        return ((CentralityAnalyzer<V>) CENTRALITY_ANALYZER).getTopHubs(graph, topN);
    }

    /**
     * Finds all vertices reachable from the origin vertex.
     */
    @SuppressWarnings("unchecked")
    public static <V> Set<V> reachableVertices(@NonNull Graph<V> graph, @NonNull V origin) {
        return ((CentralityAnalyzer<V>) CENTRALITY_ANALYZER).findReachableVertices(graph, origin);
    }

    /**
     * Calculates minimum hop distances from an origin vertex to all reachable vertices.
     */
    @SuppressWarnings("unchecked")
    public static <V> Map<V, Integer> shortestHops(@NonNull Graph<V> graph, @NonNull V origin) {
        return ((CentralityAnalyzer<V>) CENTRALITY_ANALYZER).calculateShortestHopsFrom(graph, origin);
    }

    /**
     * Computes the Minimum Spanning Tree (MST) using Kruskal's algorithm.
     */
    @SuppressWarnings("unchecked")
    public static <V> MstResult<V> minimumSpanningTree(@NonNull Graph<V> graph) {
        return ((SpanningTreeFinder<V>) KRUSKAL_MST_FINDER).computeMst(graph);
    }

    /**
     * Computes the Minimum Spanning Tree (MST) using Prim's algorithm.
     */
    @SuppressWarnings("unchecked")
    public static <V> MstResult<V> primMinimumSpanningTree(@NonNull Graph<V> graph) {
        return ((SpanningTreeFinder<V>) PRIM_MST_FINDER).computeMst(graph);
    }

    private static class UnmodifiableGraph<V> extends AbstractGraph<V> {
        private final Graph<V> delegate;

        UnmodifiableGraph(Graph<V> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void addVertex(@NonNull V vertex) {
            throw new UnsupportedOperationException("Graph is unmodifiable");
        }

        @Override
        public void addEdge(@NonNull V source, @NonNull V destination, double weight) {
            throw new UnsupportedOperationException("Graph is unmodifiable");
        }

        @Override
        public Set<V> getVertices() {
            return delegate.getVertices();
        }

        @Override
        public List<V> getNeighbors(@NonNull V vertex) {
            return delegate.getNeighbors(vertex);
        }

        @Override
        public double getEdgeWeight(@NonNull V source, @NonNull V destination) {
            return delegate.getEdgeWeight(source, destination);
        }

        @Override
        public boolean hasEdge(@NonNull V source, @NonNull V destination) {
            return delegate.hasEdge(source, destination);
        }

        @Override
        public List<V> findPath(@NonNull V start, @NonNull V end) {
            return delegate.findPath(start, end);
        }
    }

    private static final class EmptyGraph<V> extends AbstractGraph<V> {
        static final EmptyGraph<?> INSTANCE = new EmptyGraph<>();

        @Override
        public void addVertex(@NonNull V vertex) {
            throw new UnsupportedOperationException("Empty graph is unmodifiable");
        }

        @Override
        public void addEdge(@NonNull V source, @NonNull V destination, double weight) {
            throw new UnsupportedOperationException("Empty graph is unmodifiable");
        }

        @Override
        public Set<V> getVertices() {
            return Collections.emptySet();
        }

        @Override
        public List<V> getNeighbors(@NonNull V vertex) {
            return Collections.emptyList();
        }

        @Override
        public double getEdgeWeight(@NonNull V source, @NonNull V destination) {
            return Double.POSITIVE_INFINITY;
        }

        @Override
        public boolean hasEdge(@NonNull V source, @NonNull V destination) {
            return false;
        }

        @Override
        public List<V> findPath(@NonNull V start, @NonNull V end) {
            return Collections.emptyList();
        }
    }
}
