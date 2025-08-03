package online.anhht.airline.planning.adapter.inbound;

import online.anhht.airline.model.Airport;
import online.anhht.airline.planning.FlightItineraryPlan;
import online.anhht.airline.planning.port.inbound.FlightPlanningUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/planning/routes")
public class FlightRoutePlanningController {

    private final FlightPlanningUseCase planningUseCase;

    public FlightRoutePlanningController(FlightPlanningUseCase planningUseCase) {
        this.planningUseCase = planningUseCase;
    }

    @GetMapping("/itinerary/shortest")
    public ResponseEntity<ItineraryPlanDto> getShortestItinerary(
            @RequestParam String from,
            @RequestParam String to
    ) {
        Optional<FlightItineraryPlan> plan = planningUseCase.findShortestItinerary(from, to);
        return plan.map(p -> ResponseEntity.ok(ItineraryPlanDto.fromDomain(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/itinerary/alternatives")
    public ResponseEntity<List<ItineraryPlanDto>> getAlternativeItineraries(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "3") int k
    ) {
        List<FlightItineraryPlan> plans = planningUseCase.findAlternativeItineraries(from, to, k);
        List<ItineraryPlanDto> dtos = plans.stream().map(ItineraryPlanDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/itinerary/connections")
    public ResponseEntity<List<ItineraryPlanDto>> getConnectingItineraries(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "2") int maxStops
    ) {
        List<FlightItineraryPlan> plans = planningUseCase.findItinerariesWithinMaxStops(from, to, maxStops);
        List<ItineraryPlanDto> dtos = plans.stream().map(ItineraryPlanDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/network/hubs")
    public ResponseEntity<List<String>> getHubAirports(@RequestParam(defaultValue = "5") int top) {
        List<Airport> hubs = planningUseCase.getHubAirports(top);
        List<String> hubCodes = hubs.stream().map(Airport::getAirportCode).toList();
        return ResponseEntity.ok(hubCodes);
    }

    @GetMapping("/network/reachability")
    public ResponseEntity<Map<String, Object>> getReachability(@RequestParam String from) {
        Set<String> reachable = planningUseCase.getReachableAirports(from);
        Map<String, Integer> hops = planningUseCase.getShortestHops(from);
        return ResponseEntity.ok(Map.of(
                "origin", from.toUpperCase(),
                "reachableCount", reachable.size(),
                "reachableAirports", reachable,
                "shortestHops", hops
        ));
    }

    public record ItineraryPlanDto(
            List<String> flightNumbers,
            List<String> airportSequence,
            List<String> transferAirports,
            int layoverCount,
            double totalDistanceKm,
            long totalDurationMinutes,
            boolean isDirect
    ) {
        public static ItineraryPlanDto fromDomain(FlightItineraryPlan plan) {
            List<String> flightNumbers = plan.legs().stream().map(r -> r.getFlightNo()).toList();
            return new ItineraryPlanDto(
                    flightNumbers,
                    plan.airportSequence(),
                    plan.transferAirports(),
                    plan.layoverCount(),
                    plan.totalDistanceKm(),
                    plan.totalFlightDuration().toMinutes(),
                    plan.isDirect()
            );
        }
    }
}
