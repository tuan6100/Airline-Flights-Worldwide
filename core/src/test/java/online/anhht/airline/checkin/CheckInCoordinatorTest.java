package online.anhht.airline.checkin;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Departure Control & Check-In Coordinator Unit Tests")
class CheckInCoordinatorTest {

    private CheckInCoordinator checkInCoordinator;
    private Flight testFlight1;
    private Flight testFlight2;
    private Ticket sampleTicket;

    @BeforeEach
    void setUp() {
        checkInCoordinator = CheckInCoordinator.of();

        Airport svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.97, 37.41), ZoneId.of("Europe/Moscow"), 100);
        Airport led = Airport.of("LED", "Pulkovo", "Saint Petersburg", "Russia", new Coordinates(59.80, 30.26), ZoneId.of("Europe/Moscow"), 100);
        Airport aer = Airport.of("AER", "Sochi", "Sochi", "Russia", new Coordinates(43.44, 39.95), ZoneId.of("Europe/Moscow"), 100);

        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route route1 = Route.of("SU001", svo, led, Set.of(DayOfWeek.MONDAY), LocalTime.of(10, 0), Duration.ofHours(1), validity);
        Route route2 = Route.of("SU002", led, aer, Set.of(DayOfWeek.MONDAY), LocalTime.of(14, 0), Duration.ofHours(3), validity);

        Seat s1 = Seat.of("1A", FareCondition.BUSINESS, "A320");
        Seat s2 = Seat.of("1B", FareCondition.BUSINESS, "A320");
        Seat s3 = Seat.of("2A", FareCondition.ECONOMY, "A320");
        Seat s4 = Seat.of("2B", FareCondition.ECONOMY, "A320");
        CabinLayout layout = CabinLayout.of("A320", List.of(s1, s2, s3, s4));

        Airplane plane1 = Airplane.of("PL-01", "Airbus A320", 5000, 850, layout);
        Airplane plane2 = Airplane.of("PL-02", "Airbus A320", 5000, 850, layout);

        OffsetDateTime dep1 = OffsetDateTime.of(2026, 6, 1, 10, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime arr1 = dep1.plusHours(1);

        OffsetDateTime dep2 = OffsetDateTime.of(2026, 6, 1, 14, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime arr2 = dep2.plusHours(3);

        testFlight1 = Flight.of("FL-1001", "SU001", route1, plane1, dep1, arr1);
        testFlight2 = Flight.of("FL-1002", "SU002", route2, plane2, dep2, arr2);

        TicketFlightSegment seg1 = TicketFlightSegment.of("FL-1001", FareCondition.ECONOMY, new BigDecimal("100.00"), true);
        TicketFlightSegment seg2 = TicketFlightSegment.of("FL-1002", FareCondition.ECONOMY, new BigDecimal("150.00"), true);

        sampleTicket = Ticket.of(
                "0005432000010", "BK002B", "ID-123456", "DMITRY IVANOV", "{\"phone\":\"+79991234567\"}",
                List.of(seg1, seg2)
        );
    }

    @Test
    @DisplayName("Should enforce 24-hour check-in window")
    void testCheckInWindowValidation() {
        OffsetDateTime tooEarly = testFlight1.getScheduledDeparture().minusHours(25);
        OffsetDateTime inWindow = testFlight1.getScheduledDeparture().minusHours(12);
        OffsetDateTime afterDeparture = testFlight1.getScheduledDeparture().plusMinutes(5);

        assertFalse(checkInCoordinator.isCheckInOpen(testFlight1, tooEarly));
        assertTrue(checkInCoordinator.isCheckInOpen(testFlight1, inWindow));
        assertFalse(checkInCoordinator.isCheckInOpen(testFlight1, afterDeparture));

        assertThrows(IllegalStateException.class, () ->
                checkInCoordinator.checkInSegment(sampleTicket, testFlight1, "2A", tooEarly)
        );
    }

    @Test
    @DisplayName("Should perform through check-in issuing boarding passes for all ticket segments")
    void testThroughCheckIn() {
        OffsetDateTime now = testFlight1.getScheduledDeparture().minusHours(5);

        List<BoardingPass> boardingPasses = checkInCoordinator.throughCheckIn(
                sampleTicket,
                Map.of("FL-1001", testFlight1, "FL-1002", testFlight2),
                Map.of("FL-1001", "2A", "FL-1002", "2B"),
                now
        );

        assertEquals(2, boardingPasses.size());
        assertEquals("FL-1001", boardingPasses.get(0).getFlightId());
        assertEquals("2A", boardingPasses.get(0).getSeatNo());
        assertEquals(1, boardingPasses.get(0).getBoardingNo());

        assertEquals("FL-1002", boardingPasses.get(1).getFlightId());
        assertEquals("2B", boardingPasses.get(1).getSeatNo());
        assertEquals(1, boardingPasses.get(1).getBoardingNo());
    }

    @Test
    @DisplayName("Should strictly prevent double booking of the same seat on a flight")
    void testSeatNoDoubleBookingConstraint() {
        OffsetDateTime now = testFlight1.getScheduledDeparture().minusHours(4);

        // First passenger successfully books 2A
        BoardingPass pass1 = checkInCoordinator.checkInSegment(sampleTicket, testFlight1, "2A", now);
        assertNotNull(pass1);

        // Second passenger attempts to book the same seat 2A on the same flight
        Ticket secondTicket = Ticket.of(
                "0005432000011", "BK003C", "ID-999999", "SERGEY KOZLOV", null,
                List.of(TicketFlightSegment.of("FL-1001", FareCondition.ECONOMY, new BigDecimal("100.00"), true))
        );

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                checkInCoordinator.checkInSegment(secondTicket, testFlight1, "2A", now)
        );
        assertTrue(ex.getMessage().contains("No double-booking allowed"));
    }
}
