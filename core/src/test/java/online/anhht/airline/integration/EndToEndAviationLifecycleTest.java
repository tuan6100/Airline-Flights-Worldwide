package online.anhht.airline.integration;

import online.anhht.airline.checkin.CheckInCoordinator;
import online.anhht.airline.events.DomainEvent;
import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Booking;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.FlightStatus;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import online.anhht.airline.operations.FlightRealizer;
import online.anhht.airline.operations.ScheduleDeviationAnalyzer;
import online.anhht.airline.operations.ScheduleDeviationAnalyzer.DeviationReport;
import online.anhht.airline.scheduling.FlightSchedulerFactory;
import online.anhht.airline.scheduling.FlightSchedulingStrategy;
import online.anhht.airline.scheduling.SchedulingProblem;
import online.anhht.airline.scheduling.SchedulingSolution;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("End-To-End Cross-Domain Aviation Business Lifecycle Integration Test")
class EndToEndAviationLifecycleTest {

    @Test
    @DisplayName("Complete Aviation Business Lifecycle: Domain 1 -> Domain 2 -> Domain 3 -> Domain 4 -> Domain 5")
    void testCompleteAviationBusinessLifecycle() {
        // =========================================================================
        // DOMAIN 1: Flight Planning & Network Scheduling
        // =========================================================================
        Airport svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.9726, 37.4146), ZoneId.of("Europe/Moscow"), 150);
        Airport jfk = Airport.of("JFK", "John F. Kennedy", "New York", "USA", new Coordinates(40.6413, -73.7781), ZoneId.of("America/New_York"), 200);

        TemporalValidityRange seasonRange = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route svoJfkRoute = Route.of(
                "SU100",
                svo,
                jfk,
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                LocalTime.of(14, 0),
                Duration.ofHours(10),
                seasonRange
        );

        // =========================================================================
        // DOMAIN 3: Inventory & Cabin Capacity
        // =========================================================================
        List<Seat> seats = new ArrayList<>();
        // Business: Rows 1-2, Seats A, B, C, D
        for (int r = 1; r <= 2; r++) {
            for (String l : List.of("A", "B", "C", "D")) {
                seats.add(Seat.of(r + l, FareCondition.BUSINESS, "773"));
            }
        }
        // Comfort: Rows 3-4, Seats A, B, C, D, E, F
        for (int r = 3; r <= 4; r++) {
            for (String l : List.of("A", "B", "C", "D", "E", "F")) {
                seats.add(Seat.of(r + l, FareCondition.COMFORT, "773"));
            }
        }
        // Economy: Rows 5-30, Seats A, B, C, D, E, F
        for (int r = 5; r <= 30; r++) {
            for (String l : List.of("A", "B", "C", "D", "E", "F")) {
                seats.add(Seat.of(r + l, FareCondition.ECONOMY, "773"));
            }
        }
        CabinLayout layout773 = CabinLayout.of("773", seats);
        Airplane b777 = Airplane.of("PL-773-01", "Boeing 777-300", 11100, 905, layout773);

        assertTrue(svoJfkRoute.isQualifiedAircraft(b777), "Boeing 777-300 must be qualified for transatlantic route");
        assertEquals(8, layout773.getCapacity(FareCondition.BUSINESS));
        assertEquals(12, layout773.getCapacity(FareCondition.COMFORT));
        assertEquals(156, layout773.getCapacity(FareCondition.ECONOMY));

        // Scheduling optimization via Strategy Factory
        FlightSchedulingStrategy scheduler = FlightSchedulerFactory.getInstance().createSimulatedAnnealingScheduler();
        SchedulingSolution scheduleSol = scheduler.schedule(new SchedulingProblem(
                List.of(svoJfkRoute), List.of(b777), LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 7)
        ));
        assertTrue(scheduleSol.getScheduledFlightCount() > 0);

        // =========================================================================
        // DOMAIN 2: Flight Operations - Realization 60 days in advance
        // =========================================================================
        FlightRealizer flightRealizer = FlightRealizer.of();
        LocalDate today = LocalDate.of(2026, 4, 2); // 60 days before 2026-06-01 (Monday)
        List<Flight> realizedFlights = flightRealizer.realizeFlights60DaysAdvance(svoJfkRoute, b777, today);

        assertFalse(realizedFlights.isEmpty());
        Flight flight = realizedFlights.get(0);
        assertEquals("SU100", flight.getFlightNo());
        assertEquals(FlightStatus.SCHEDULED, flight.getStatus());

        // Moscow and New York local timetable checks
        assertEquals("Europe/Moscow", flight.getScheduledDepartureLocal().getZone().getId());
        assertEquals("America/New_York", flight.getScheduledArrivalLocal().getZone().getId());

        // =========================================================================
        // DOMAIN 4: Sales, Booking & Ticketing
        // =========================================================================
        // Group booking for 2 passengers (Ivan and Elena)
        TicketFlightSegment segment1 = TicketFlightSegment.of(flight.getFlightId(), FareCondition.BUSINESS, new BigDecimal("850.00"), true);
        TicketFlightSegment segment2 = TicketFlightSegment.of(flight.getFlightId(), FareCondition.BUSINESS, new BigDecimal("850.00"), true);

        Ticket ticket1 = Ticket.of(
                "0005432000001",
                "BK007A",
                "PASS-998877",
                "IVAN VORONOV",
                "{\"phone\":\"+79990001122\"}",
                List.of(segment1)
        );

        Ticket ticket2 = Ticket.of(
                "0005432000002",
                "BK007A",
                "PASS-998878",
                "ELENA VORONOVA",
                "{\"phone\":\"+79990001123\"}",
                List.of(segment2)
        );

        Booking groupBooking = Booking.of("BK007A", OffsetDateTime.now(), List.of(ticket1, ticket2));
        assertEquals(new BigDecimal("1700.00"), groupBooking.getTotalAmount());
        assertEquals(2, groupBooking.getPassengerCount());

        // =========================================================================
        // DOMAIN 5: Departure Control & Check-In
        // =========================================================================
        CheckInCoordinator checkInCoordinator = CheckInCoordinator.of();

        // 1. Check window too early (e.g., 30 hours before departure) -> Must be rejected
        OffsetDateTime tooEarlyTime = flight.getScheduledDeparture().minusHours(30);
        assertFalse(checkInCoordinator.isCheckInOpen(flight, tooEarlyTime));

        // 2. Check window open (20 hours before departure) -> Must succeed
        OffsetDateTime checkInTime = flight.getScheduledDeparture().minusHours(20);
        assertTrue(checkInCoordinator.isCheckInOpen(flight, checkInTime));

        // 3. Check in Passenger 1 to Seat 1A
        BoardingPass bp1 = checkInCoordinator.checkInSegment(ticket1, flight, "1A", checkInTime);
        assertNotNull(bp1);
        assertEquals("1A", bp1.getSeatNo());
        assertEquals(1, bp1.getBoardingNo());

        // 4. Concurrency & No-Double-Booking: Passenger 2 tries to choose the same Seat 1A
        assertThrows(IllegalStateException.class, () ->
                checkInCoordinator.checkInSegment(ticket2, flight, "1A", checkInTime)
        );

        // 5. Passenger 2 chooses Seat 1B -> Must succeed with sequential boarding number 2
        BoardingPass bp2 = checkInCoordinator.checkInSegment(ticket2, flight, "1B", checkInTime);
        assertNotNull(bp2);
        assertEquals("1B", bp2.getSeatNo());
        assertEquals(2, bp2.getBoardingNo());

        // =========================================================================
        // DOMAIN 2: Flight Operations - Operational Execution
        // =========================================================================
        // Flight goes from SCHEDULED -> ON_TIME -> BOARDING -> DEPARTED -> ARRIVED
        flight.markOnTime();
        assertEquals(FlightStatus.ON_TIME, flight.getStatus());

        flight.startBoarding();
        assertEquals(FlightStatus.BOARDING, flight.getStatus());

        OffsetDateTime actualDep = flight.getScheduledDeparture().plusMinutes(10);
        flight.depart(actualDep);
        assertEquals(FlightStatus.DEPARTED, flight.getStatus());
        assertEquals(actualDep, flight.getActualDeparture());

        OffsetDateTime actualArr = flight.getScheduledArrival().plusMinutes(12);
        flight.arrive(actualArr);
        assertEquals(FlightStatus.ARRIVED, flight.getStatus());
        assertEquals(actualArr, flight.getActualArrival());

        // Delay / Deviation analyzer
        ScheduleDeviationAnalyzer deviationAnalyzer = ScheduleDeviationAnalyzer.of();
        DeviationReport report = deviationAnalyzer.analyzeDeviation(flight);
        assertEquals(10, report.departureDelayMinutes());
        assertEquals(12, report.arrivalDelayMinutes());
        assertEquals(ScheduleDeviationAnalyzer.DelayCategory.MINOR_DELAY, report.delayCategory());
    }
}
