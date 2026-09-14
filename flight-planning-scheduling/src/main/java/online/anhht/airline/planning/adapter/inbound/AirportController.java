package online.anhht.airline.planning.adapter.inbound;

import online.anhht.airline.model.Airport;
import online.anhht.airline.planning.port.inbound.FlightPlanningUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/airports")
public class AirportController {

    private final FlightPlanningUseCase flightPlanningUseCase;

    public AirportController(FlightPlanningUseCase flightPlanningUseCase) {
        this.flightPlanningUseCase = flightPlanningUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AirportResponse>> getAllAirports() {
        List<AirportResponse> list = flightPlanningUseCase.getAllAirports().stream()
                .map(AirportResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{code}")
    public ResponseEntity<AirportResponse> getAirport(@PathVariable String code) {
        return flightPlanningUseCase.getAirportByCode(code)
                .map(AirportResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/distance")
    public ResponseEntity<Map<String, Object>> getDistance(
            @RequestParam String from,
            @RequestParam String to
    ) {
        double distance = flightPlanningUseCase.calculateDistance(from, to);
        return ResponseEntity.ok(Map.of(
                "fromAirport", from.toUpperCase(),
                "toAirport", to.toUpperCase(),
                "distanceKm", distance
        ));
    }

    public record AirportResponse(
            String airportCode,
            String airportName,
            String city,
            String country,
            double latitude,
            double longitude,
            String timezone,
            int capacity,
            int parkedAircraftCount
    ) {
        public static AirportResponse fromDomain(Airport a) {
            return new AirportResponse(
                    a.getAirportCode(),
                    a.getAirportName(),
                    a.getCity(),
                    a.getCountry(),
                    a.getCoordinates().latitude(),
                    a.getCoordinates().longitude(),
                    a.getTimezone().getId(),
                    a.getCapacity(),
                    a.getParkedAirplaneCount()
            );
        }
    }
}
