package online.anhht.airline.planning.port.inbound;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.planning.FlightItineraryPlan;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface FlightPlanningUseCase {
    List<Airport> getAllAirports();
    Optional<Airport> getAirportByCode(String airportCode);
    double calculateDistance(String fromCode, String toCode);

    List<Airplane> getAllAirplanes();
    Optional<Airplane> getAirplaneById(String id);
    boolean checkAircraftQualification(String airplaneId, double distanceKm);

    List<Route> getAllRoutes();
    Optional<Route> getRouteByFlightNo(String flightNo);
    List<String> findShortestRoutePath(String departureCode, String arrivalCode);

    Optional<FlightItineraryPlan> findShortestItinerary(String originCode, String destinationCode);
    List<FlightItineraryPlan> findAlternativeItineraries(String originCode, String destinationCode, int maxAlternatives);
    List<FlightItineraryPlan> findItinerariesWithinMaxStops(String originCode, String destinationCode, int maxStops);
    List<Airport> getHubAirports(int topN);
    Set<String> getReachableAirports(String originCode);
    Map<String, Integer> getShortestHops(String originCode);
}
