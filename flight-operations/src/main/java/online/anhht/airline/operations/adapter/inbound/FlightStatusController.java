package online.anhht.airline.operations.adapter.inbound;

import online.anhht.airline.model.Flight;
import online.anhht.airline.operations.port.inbound.FlightOperationsUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/operations/flights")
public class FlightStatusController {

    private final FlightOperationsUseCase operationsUseCase;

    public FlightStatusController(FlightOperationsUseCase operationsUseCase) {
        this.operationsUseCase = operationsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<FlightDto>> getAllFlights() {
        List<FlightDto> list = operationsUseCase.getAllFlights().stream()
                .map(FlightDto::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<FlightDto> getFlight(@PathVariable String flightId) {
        return operationsUseCase.getFlightById(flightId)
                .map(FlightDto::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{flightId}/on-time")
    public ResponseEntity<FlightDto> setOnTime(@PathVariable String flightId) {
        Flight flight = operationsUseCase.updateFlightToOnTime(flightId);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    @PostMapping("/{flightId}/delay")
    public ResponseEntity<FlightDto> delayFlight(
            @PathVariable String flightId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime expectedDeparture,
            @RequestParam(defaultValue = "Operational reason") String reason
    ) {
        Flight flight = operationsUseCase.updateFlightToDelayed(flightId, expectedDeparture, reason);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    @PostMapping("/{flightId}/boarding")
    public ResponseEntity<FlightDto> startBoarding(@PathVariable String flightId) {
        Flight flight = operationsUseCase.updateFlightToBoarding(flightId);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    @PostMapping("/{flightId}/depart")
    public ResponseEntity<FlightDto> departFlight(
            @PathVariable String flightId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime actualDeparture
    ) {
        OffsetDateTime departureTime = actualDeparture != null ? actualDeparture : OffsetDateTime.now();
        Flight flight = operationsUseCase.updateFlightToDeparted(flightId, departureTime);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    @PostMapping("/{flightId}/arrive")
    public ResponseEntity<FlightDto> arriveFlight(
            @PathVariable String flightId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime actualArrival
    ) {
        OffsetDateTime arrivalTime = actualArrival != null ? actualArrival : OffsetDateTime.now();
        Flight flight = operationsUseCase.updateFlightToArrived(flightId, arrivalTime);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    @PostMapping("/{flightId}/cancel")
    public ResponseEntity<FlightDto> cancelFlight(
            @PathVariable String flightId,
            @RequestParam(defaultValue = "Weather condition") String reason
    ) {
        Flight flight = operationsUseCase.cancelFlight(flightId, reason);
        return ResponseEntity.ok(FlightDto.fromDomain(flight));
    }

    public record FlightDto(
            String flightId,
            String flightNo,
            String departureAirport,
            String arrivalAirport,
            String status,
            String scheduledDeparture,
            String scheduledArrival,
            String actualDeparture,
            String actualArrival,
            String delayReason,
            String cancelReason
    ) {
        public static FlightDto fromDomain(Flight f) {
            return new FlightDto(
                    f.getFlightId(),
                    f.getFlightNo(),
                    f.getRoute().getDepartureAirport().getAirportCode(),
                    f.getRoute().getArrivalAirport().getAirportCode(),
                    f.getStatus().name(),
                    f.getScheduledDeparture().toString(),
                    f.getScheduledArrival().toString(),
                    f.getActualDeparture() != null ? f.getActualDeparture().toString() : null,
                    f.getActualArrival() != null ? f.getActualArrival().toString() : null,
                    f.getDelayReason(),
                    f.getCancelReason()
            );
        }
    }
}
