package online.anhht.airline.planning.application;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.planning.FlightItineraryPlan;
import online.anhht.airline.planning.FlightRoutePlanner;
import online.anhht.airline.planning.port.inbound.FlightPlanningUseCase;
import online.anhht.airline.planning.port.outbound.AirportRepositoryPort;
import online.anhht.airline.planning.port.outbound.FleetRepositoryPort;
import online.anhht.airline.planning.port.outbound.RouteRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class FlightPlanningApplicationService implements FlightPlanningUseCase {

    private final AirportRepositoryPort airportRepo;
    private final FleetRepositoryPort fleetRepo;
    private final RouteRepositoryPort routeRepo;

    public FlightPlanningApplicationService(
            AirportRepositoryPort airportRepo,
            FleetRepositoryPort fleetRepo,
            RouteRepositoryPort routeRepo
    ) {
        this.airportRepo = airportRepo;
        this.fleetRepo = fleetRepo;
        this.routeRepo = routeRepo;
    }

    private FlightRoutePlanner getRoutePlanner() {
        return FlightRoutePlanner.of(airportRepo.findAll(), routeRepo.findAll());
    }

    @Override
    public List<Airport> getAllAirports() {
        return airportRepo.findAll();
    }

    @Override
    public Optional<Airport> getAirportByCode(String airportCode) {
        return airportRepo.findByCode(airportCode);
    }

    @Override
    public double calculateDistance(String fromCode, String toCode) {
        Airport from = airportRepo.findByCode(fromCode)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + fromCode));
        Airport to = airportRepo.findByCode(toCode)
                .orElseThrow(() -> new IllegalArgumentException("Airport not found: " + toCode));
        return from.distanceTo(to);
    }

    @Override
    public List<Airplane> getAllAirplanes() {
        return fleetRepo.findAll();
    }

    @Override
    public Optional<Airplane> getAirplaneById(String id) {
        return fleetRepo.findById(id);
    }

    @Override
    public boolean checkAircraftQualification(String airplaneId, double distanceKm) {
        Airplane plane = fleetRepo.findById(airplaneId)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft not found: " + airplaneId));
        return plane.isRangeQualified(distanceKm);
    }

    @Override
    public List<Route> getAllRoutes() {
        return routeRepo.findAll();
    }

    @Override
    public Optional<Route> getRouteByFlightNo(String flightNo) {
        return routeRepo.findByFlightNo(flightNo);
    }

    @Override
    public List<String> findShortestRoutePath(String departureCode, String arrivalCode) {
        Optional<FlightItineraryPlan> plan = getRoutePlanner().findShortestItinerary(departureCode, arrivalCode);
        return plan.map(FlightItineraryPlan::airportSequence).orElse(List.of());
    }

    @Override
    public Optional<FlightItineraryPlan> findShortestItinerary(String originCode, String destinationCode) {
        return getRoutePlanner().findShortestItinerary(originCode, destinationCode);
    }

    @Override
    public List<FlightItineraryPlan> findAlternativeItineraries(String originCode, String destinationCode, int maxAlternatives) {
        return getRoutePlanner().findAlternativeItineraries(originCode, destinationCode, maxAlternatives);
    }

    @Override
    public List<FlightItineraryPlan> findItinerariesWithinMaxStops(String originCode, String destinationCode, int maxStops) {
        return getRoutePlanner().findItinerariesWithinMaxStops(originCode, destinationCode, maxStops);
    }

    @Override
    public List<Airport> getHubAirports(int topN) {
        return getRoutePlanner().identifyHubAirports(topN);
    }

    @Override
    public Set<String> getReachableAirports(String originCode) {
        return getRoutePlanner().findReachableAirports(originCode);
    }

    @Override
    public Map<String, Integer> getShortestHops(String originCode) {
        return getRoutePlanner().calculateShortestHopsFrom(originCode);
    }
}
