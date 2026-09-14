package online.anhht.airline.operations.adapter.inbound;

import online.anhht.airline.model.Flight;
import online.anhht.airline.operations.port.inbound.FlightOperationsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/operations/timetable")
public class TimetableController {

    private final FlightOperationsUseCase operationsUseCase;

    public TimetableController(FlightOperationsUseCase operationsUseCase) {
        this.operationsUseCase = operationsUseCase;
    }

    @GetMapping
    public ResponseEntity<List<TimetableItem>> getTimetable() {
        List<TimetableItem> timetable = operationsUseCase.getAllFlights().stream()
                .map(TimetableItem::fromFlight)
                .toList();
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<TimetableItem> getFlightTimetable(@PathVariable String flightId) {
        return operationsUseCase.getFlightById(flightId)
                .map(TimetableItem::fromFlight)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record TimetableItem(
            String flightId,
            String flightNo,
            String departureAirportCode,
            String arrivalAirportCode,
            String status,
            // UTC times
            String scheduledDepartureUtc,
            String scheduledArrivalUtc,
            // Local airport times
            String scheduledDepartureLocal,
            String departureTimezone,
            String scheduledArrivalLocal,
            String arrivalTimezone
    ) {
        public static TimetableItem fromFlight(Flight f) {
            return new TimetableItem(
                    f.getFlightId(),
                    f.getFlightNo(),
                    f.getRoute().getDepartureAirport().getAirportCode(),
                    f.getRoute().getArrivalAirport().getAirportCode(),
                    f.getStatus().name(),
                    f.getScheduledDepartureUtc().toString(),
                    f.getScheduledArrivalUtc().toString(),
                    f.getScheduledDepartureLocal().toString(),
                    f.getRoute().getDepartureAirport().getTimezone().getId(),
                    f.getScheduledArrivalLocal().toString(),
                    f.getRoute().getArrivalAirport().getTimezone().getId()
            );
        }
    }
}
