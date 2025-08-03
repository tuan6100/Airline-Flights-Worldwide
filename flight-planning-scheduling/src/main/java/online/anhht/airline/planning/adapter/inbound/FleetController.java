package online.anhht.airline.planning.adapter.inbound;

import online.anhht.airline.model.Airplane;
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
@RequestMapping("/api/v1/fleet")
public class FleetController {

    private final FlightPlanningUseCase flightPlanningUseCase;

    public FleetController(FlightPlanningUseCase flightPlanningUseCase) {
        this.flightPlanningUseCase = flightPlanningUseCase;
    }

    @GetMapping
    public ResponseEntity<List<AirplaneResponse>> getAllFleet() {
        List<AirplaneResponse> list = flightPlanningUseCase.getAllAirplanes().stream()
                .map(AirplaneResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AirplaneResponse> getAirplane(@PathVariable String id) {
        return flightPlanningUseCase.getAirplaneById(id)
                .map(AirplaneResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/qualification")
    public ResponseEntity<Map<String, Object>> checkQualification(
            @PathVariable String id,
            @RequestParam double distanceKm
    ) {
        boolean qualified = flightPlanningUseCase.checkAircraftQualification(id, distanceKm);
        return ResponseEntity.ok(Map.of(
                "airplaneId", id,
                "distanceKm", distanceKm,
                "isQualified", qualified
        ));
    }

    public record AirplaneResponse(
            String id,
            String model,
            int rangeKm,
            int cruisingSpeedKmH,
            int seatingCapacity,
            String state
    ) {
        public static AirplaneResponse fromDomain(Airplane a) {
            return new AirplaneResponse(
                    a.getId(),
                    a.getModel(),
                    a.getRangeKm(),
                    a.getCruisingSpeedKmH(),
                    a.getSeatingCapacity(),
                    a.getState().getStateName()
            );
        }
    }
}
