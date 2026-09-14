package online.anhht.airline.checkin.adapter.inbound;

import online.anhht.airline.checkin.port.inbound.CheckInUseCase;
import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/checkin")
public class CheckInController {

    private final CheckInUseCase checkInUseCase;

    public CheckInController(CheckInUseCase checkInUseCase) {
        this.checkInUseCase = checkInUseCase;
    }

    private Flight createMockFlight(String flightId, OffsetDateTime departureTime) {
        Airport svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.97, 37.41), ZoneId.of("Europe/Moscow"), 100);
        Airport led = Airport.of("LED", "Pulkovo", "St Petersburg", "Russia", new Coordinates(59.80, 30.26), ZoneId.of("Europe/Moscow"), 100);
        TemporalValidityRange val = new TemporalValidityRange(OffsetDateTime.now().minusDays(10), OffsetDateTime.now().plusDays(100));
        Route r = Route.of("SU001", svo, led, Set.of(DayOfWeek.values()), LocalTime.of(10, 0), Duration.ofHours(1), val);

        List<Seat> seats = List.of(
                Seat.of("1A", FareCondition.BUSINESS, "320"),
                Seat.of("1B", FareCondition.BUSINESS, "320"),
                Seat.of("2A", FareCondition.ECONOMY, "320"),
                Seat.of("2B", FareCondition.ECONOMY, "320")
        );
        Airplane plane = Airplane.of("320-001", "Airbus A320", 5000, 850, CabinLayout.of("320", seats));
        return Flight.of(flightId, "SU001", r, plane, departureTime, departureTime.plusHours(1));
    }

    @PostMapping("/segment")
    public ResponseEntity<BoardingPassDto> checkInSingleSegment(@RequestBody SingleCheckInRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime flightDep = now.plusHours(2); // open window
        Flight flight = createMockFlight(request.flightId(), flightDep);

        Ticket ticket = Ticket.of(
                request.ticketNo(),
                "BK001A",
                request.passengerId(),
                request.passengerName(),
                null,
                List.of(TicketFlightSegment.of(request.flightId(), FareCondition.ECONOMY, BigDecimal.valueOf(100), true))
        );

        BoardingPass pass = checkInUseCase.checkIn(ticket, flight, request.seatNo(), now);
        return ResponseEntity.ok(BoardingPassDto.fromDomain(pass));
    }

    @PostMapping("/through")
    public ResponseEntity<List<BoardingPassDto>> throughCheckIn(@RequestBody ThroughCheckInRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime flight1Dep = now.plusHours(2);
        OffsetDateTime flight2Dep = now.plusHours(6);

        Flight f1 = createMockFlight(request.flight1Id(), flight1Dep);
        Flight f2 = createMockFlight(request.flight2Id(), flight2Dep);

        Ticket ticket = Ticket.of(
                request.ticketNo(),
                "BK001A",
                request.passengerId(),
                request.passengerName(),
                null,
                List.of(
                        TicketFlightSegment.of(request.flight1Id(), FareCondition.ECONOMY, BigDecimal.valueOf(100), true),
                        TicketFlightSegment.of(request.flight2Id(), FareCondition.ECONOMY, BigDecimal.valueOf(150), true)
                )
        );

        Map<String, Flight> flightsMap = Map.of(request.flight1Id(), f1, request.flight2Id(), f2);
        Map<String, String> seatPrefs = Map.of(request.flight1Id(), request.seat1No(), request.flight2Id(), request.seat2No());

        List<BoardingPass> passes = checkInUseCase.throughCheckIn(ticket, flightsMap, seatPrefs, now);
        List<BoardingPassDto> dtos = passes.stream().map(BoardingPassDto::fromDomain).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/window")
    public ResponseEntity<Map<String, Object>> checkWindow(
            @RequestParam String flightId,
            @RequestParam int hoursUntilDeparture
    ) {
        OffsetDateTime dep = OffsetDateTime.now().plusHours(hoursUntilDeparture);
        Flight flight = createMockFlight(flightId, dep);
        boolean isOpen = checkInUseCase.isCheckInOpen(flight, OffsetDateTime.now());

        return ResponseEntity.ok(Map.of(
                "flightId", flightId,
                "hoursUntilDeparture", hoursUntilDeparture,
                "isCheckInOpen", isOpen
        ));
    }

    public record SingleCheckInRequest(
            String ticketNo,
            String passengerId,
            String passengerName,
            String flightId,
            String seatNo
    ) {}

    public record ThroughCheckInRequest(
            String ticketNo,
            String passengerId,
            String passengerName,
            String flight1Id,
            String seat1No,
            String flight2Id,
            String seat2No
    ) {}

    public record BoardingPassDto(
            String ticketNo,
            String flightId,
            int boardingNo,
            String seatNo,
            String boardingTime
    ) {
        public static BoardingPassDto fromDomain(BoardingPass bp) {
            return new BoardingPassDto(
                    bp.getTicketNo(),
                    bp.getFlightId(),
                    bp.getBoardingNo(),
                    bp.getSeatNo(),
                    bp.getBoardingTime().toString()
            );
        }
    }
}
