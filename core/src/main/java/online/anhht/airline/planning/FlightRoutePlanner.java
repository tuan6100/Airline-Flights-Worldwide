package online.anhht.airline.planning;

import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.util.graph.Graph;
import online.anhht.airline.util.graph.Graphs;
import online.anhht.airline.util.graph.Heuristic;
import online.anhht.airline.util.graph.NetworkCentralityAnalyzer;
import online.anhht.airline.util.graph.WeightedPath;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * High-performance graph-based flight route planner and network analytics engine.
 */
public class FlightRoutePlanner {

    private final Map<String, Airport> airportMap;
    private final Map<String, Map<String, Route>> directRouteMap;
    private final Graph<String> routeGraph;
    private final NetworkCentralityAnalyzer<String> centralityAnalyzer;

    public FlightRoutePlanner(@NonNull Collection<Airport> airports, @NonNull Collection<Route> routes) {
        Objects.requireNonNull(airports, "Airports must not be null");
        Objects.requireNonNull(routes, "Routes must not be null");

        this.airportMap = new HashMap<>();
        for (Airport airport : airports) {
            this.airportMap.put(airport.getAirportCode(), airport);
        }

        this.directRouteMap = new HashMap<>();
        this.routeGraph = Graph.of();

        for (Airport airport : airports) {
            this.routeGraph.addVertex(airport.getAirportCode());
        }

        for (Route route : routes) {
            String dep = route.getDepartureAirport().getAirportCode();
            String arr = route.getArrivalAirport().getAirportCode();
            this.airportMap.putIfAbsent(dep, route.getDepartureAirport());
            this.airportMap.putIfAbsent(arr, route.getArrivalAirport());

            this.routeGraph.addEdge(dep, arr, route.getDistanceKm());
            this.directRouteMap.computeIfAbsent(dep, k -> new HashMap<>()).put(arr, route);
        }

        this.centralityAnalyzer = new NetworkCentralityAnalyzer<>();
    }

    public static FlightRoutePlanner of(Collection<Airport> airports, Collection<Route> routes) {
        return new FlightRoutePlanner(airports, routes);
    }

    /**
     * Finds the shortest flight itinerary between origin and destination airports using A* search with Haversine distance heuristic.
     */
    public Optional<FlightItineraryPlan> findShortestItinerary(@NonNull String originCode, @NonNull String destinationCode) {
        String from = normalizeCode(originCode);
        String to = normalizeCode(destinationCode);

        Heuristic<String> haversineHeuristic = (u, target) -> {
            Airport a1 = airportMap.get(u);
            Airport a2 = airportMap.get(target);
            if (a1 != null && a2 != null) {
                return a1.distanceTo(a2);
            }
            return 0.0;
        };

        WeightedPath<String> path = Graphs.aStar(routeGraph, from, to, haversineHeuristic);

        if (path.isEmpty() || path.vertices().size() < 2) {
            return Optional.empty();
        }

        return Optional.ofNullable(buildPlanFromAirportSequence(path.vertices()));
    }

    /**
     * Finds top-K alternative flight itineraries using Yen's K-shortest paths algorithm.
     */
    public List<FlightItineraryPlan> findAlternativeItineraries(@NonNull String originCode, @NonNull String destinationCode, int maxAlternatives) {
        String from = normalizeCode(originCode);
        String to = normalizeCode(destinationCode);

        List<WeightedPath<String>> kPaths = Graphs.kShortestPaths(routeGraph, from, to, maxAlternatives);

        List<FlightItineraryPlan> plans = new ArrayList<>();
        for (WeightedPath<String> path : kPaths) {
            FlightItineraryPlan plan = buildPlanFromAirportSequence(path.vertices());
            if (plan != null) {
                plans.add(plan);
            }
        }

        return Collections.unmodifiableList(plans);
    }

    /**
     * Discovers all flight itineraries within a maximum allowable number of stops/transfers.
     */
    public List<FlightItineraryPlan> findItinerariesWithinMaxStops(@NonNull String originCode, @NonNull String destinationCode, int maxStops) {
        String from = normalizeCode(originCode);
        String to = normalizeCode(destinationCode);

        List<WeightedPath<String>> paths = Graphs.allPaths(routeGraph, from, to, maxStops + 1);

        List<FlightItineraryPlan> plans = new ArrayList<>();
        for (WeightedPath<String> path : paths) {
            FlightItineraryPlan plan = buildPlanFromAirportSequence(path.vertices());
            if (plan != null) {
                plans.add(plan);
            }
        }

        return Collections.unmodifiableList(plans);
    }

    /**
     * Identifies top hub airports based on Betweenness Centrality in the global flight network.
     */
    public List<Airport> identifyHubAirports(int topN) {
        List<String> hubCodes = centralityAnalyzer.getTopHubs(routeGraph, topN);
        List<Airport> hubs = new ArrayList<>();
        for (String code : hubCodes) {
            Airport a = airportMap.get(code);
            if (a != null) {
                hubs.add(a);
            }
        }
        return Collections.unmodifiableList(hubs);
    }

    /**
     * Returns all airports reachable from the specified origin airport.
     */
    public Set<String> findReachableAirports(@NonNull String originCode) {
        return centralityAnalyzer.findReachableVertices(routeGraph, normalizeCode(originCode));
    }

    /**
     * Calculates the minimum hop distances from an origin airport to all reachable airports.
     */
    public Map<String, Integer> calculateShortestHopsFrom(@NonNull String originCode) {
        return centralityAnalyzer.calculateShortestHopsFrom(routeGraph, normalizeCode(originCode));
    }

    private FlightItineraryPlan buildPlanFromAirportSequence(List<String> airports) {
        if (airports == null || airports.size() < 2) {
            return null;
        }
        List<Route> legs = new ArrayList<>();
        for (int i = 0; i < airports.size() - 1; i++) {
            String u = airports.get(i);
            String v = airports.get(i + 1);
            Map<String, Route> nextMap = directRouteMap.get(u);
            if (nextMap == null || !nextMap.containsKey(v)) {
                return null;
            }
            legs.add(nextMap.get(v));
        }
        return FlightItineraryPlan.of(legs);
    }

    private String normalizeCode(String code) {
        Objects.requireNonNull(code, "Airport code must not be null");
        return code.trim().toUpperCase();
    }
}
