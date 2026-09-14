package online.anhht.airline.planning.adapter.inbound;

import online.anhht.airline.model.Route;
import online.anhht.airline.planning.port.inbound.FlightPlanningUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final FlightPlanningUseCase flightPlanningUseCase;

    public RouteController(FlightPlanningUseCase flightPlanningUseCase) {
        this.flightPlanningUseCase = flightPlanningUseCase;
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        List<RouteResponse> list = flightPlanningUseCase.getAllRoutes().stream()
                .map(RouteResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{flightNo}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable String flightNo) {
        return flightPlanningUseCase.getRouteByFlightNo(flightNo)
                .map(RouteResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/find-path")
    public ResponseEntity<Map<String, Object>> findRoutePath(
            @RequestParam String departure,
            @RequestParam String arrival
    ) {
        List<String> path = flightPlanningUseCase.findShortestRoutePath(departure, arrival);
        return ResponseEntity.ok(Map.of(
                "departureAirport", departure.toUpperCase(),
                "arrivalAirport", arrival.toUpperCase(),
                "path", path,
                "found", !path.isEmpty()
        ));
    }

    public record RouteResponse(
            String flightNo,
            String departureAirportCode,
            String arrivalAirportCode,
            Set<String> operatingDays,
            String scheduledDepartureTime,
            long scheduledDurationMinutes,
            double distanceKm,
            String validFrom,
            String validTo
    ) {
        public static RouteResponse fromDomain(Route r) {
            return new RouteResponse(
                    r.getFlightNo(),
                    r.getDepartureAirport().getAirportCode(),
                    r.getArrivalAirport().getAirportCode(),
                    r.getDaysOfWeek().stream().map(Enum::name).collect(Collectors.toSet()),
                    r.getScheduledDepartureTime().toString(),
                    r.getScheduledDuration().toMinutes(),
                    r.getDistanceKm(),
                    r.getValidityRange().validFrom().toString(),
                    r.getValidityRange().validTo().toString()
            );
        }
    }
}
