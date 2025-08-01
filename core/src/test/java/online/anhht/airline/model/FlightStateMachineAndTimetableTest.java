package online.anhht.airline.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight State Machine, Delay Analytics, and Timetable Unit Tests")
class FlightStateMachineAndTimetableTest {

    private Flight createTestFlight() {
        Airport dep = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.9726, 37.4146), ZoneId.of("Europe/Moscow"), 100);
        Airport arr = Airport.of("JFK", "JFK Airport", "New York", "USA", new Coordinates(40.6413, -73.7781), ZoneId.of("America/New_York"), 100);

        TemporalValidityRange validityRange = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route route = Route.of("SU100", dep, arr, Set.of(DayOfWeek.MONDAY), LocalTime.of(14, 0), Duration.ofHours(10), validityRange);

        Seat seat = Seat.of("1A", FareCondition.ECONOMY, "773");
        CabinLayout layout = CabinLayout.of("773", List.of(seat));
        Airplane airplane = Airplane.of("AIRCRAFT-01", "Boeing 777", 10000, 900, layout);

        OffsetDateTime depTime = OffsetDateTime.of(2026, 6, 1, 14, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime arrTime = depTime.plusHours(10);

        return Flight.of("FLIGHT-101", "SU100", route, airplane, depTime, arrTime);
    }

    @Test
    @DisplayName("Should execute valid happy-path state transitions: SCHEDULED -> ON_TIME -> BOARDING -> DEPARTED -> ARRIVED")
    void testHappyPathStateTransitions() {
        Flight flight = createTestFlight();
        assertEquals(FlightStatus.SCHEDULED, flight.getStatus());

        flight.markOnTime();
        assertEquals(FlightStatus.ON_TIME, flight.getStatus());

        flight.startBoarding();
        assertEquals(FlightStatus.BOARDING, flight.getStatus());

        OffsetDateTime actualDep = flight.getScheduledDeparture().plusMinutes(5);
        flight.depart(actualDep);
        assertEquals(FlightStatus.DEPARTED, flight.getStatus());
        assertEquals(actualDep, flight.getActualDeparture());

        OffsetDateTime actualArr = flight.getScheduledArrival().plusMinutes(10);
        flight.arrive(actualArr);
        assertEquals(FlightStatus.ARRIVED, flight.getStatus());
        assertEquals(actualArr, flight.getActualArrival());
    }

    @Test
    @DisplayName("Should support delay state and delay deviation monitoring")
    void testDelayedFlightStateAndDeviation() {
        Flight flight = createTestFlight();

        OffsetDateTime delayedDep = flight.getScheduledDeparture().plusMinutes(45);
        flight.markDelayed(delayedDep, "Severe Thunderstorm");

        assertEquals(FlightStatus.DELAYED, flight.getStatus());
        assertEquals(45, flight.getDepartureDelayMinutes());
        assertTrue(flight.isDelayed());
        assertEquals("Severe Thunderstorm", flight.getDelayReason());

        flight.startBoarding();
        assertEquals(FlightStatus.BOARDING, flight.getStatus());
    }

    @Test
    @DisplayName("Should enforce illegal transition guards")
    void testIllegalStateTransitions() {
        Flight flight = createTestFlight();

        // Cannot depart before boarding
        assertThrows(IllegalStateException.class, () -> flight.depart(OffsetDateTime.now()));

        flight.cancel("Air traffic control strike");
        assertEquals(FlightStatus.CANCELLED, flight.getStatus());

        // Cancelled flight cannot board or depart
        assertThrows(IllegalStateException.class, flight::startBoarding);
        assertThrows(IllegalStateException.class, () -> flight.depart(OffsetDateTime.now()));
    }

    @Test
    @DisplayName("Should accurately synchronize UTC and local airport timezones in timetable view")
    void testTimetableTimezoneSynchronization() {
        Flight flight = createTestFlight();

        ZonedDateTime depUtc = flight.getScheduledDepartureUtc();
        ZonedDateTime depLocal = flight.getScheduledDepartureLocal();
        ZonedDateTime arrLocal = flight.getScheduledArrivalLocal();

        assertEquals(ZoneOffset.UTC, depUtc.getZone());
        assertEquals(ZoneId.of("Europe/Moscow"), depLocal.getZone());
        assertEquals(ZoneId.of("America/New_York"), arrLocal.getZone());

        // Moscow is UTC+3
        assertEquals(17, depLocal.getHour()); // 14:00 UTC + 3 = 17:00 MSK
        // New York is UTC-4 in summer (EDT)
        assertEquals(20, arrLocal.getHour()); // 14:00 UTC + 10h = 24:00 UTC (00:00 next day) - 4h = 20:00 NY
    }
}
