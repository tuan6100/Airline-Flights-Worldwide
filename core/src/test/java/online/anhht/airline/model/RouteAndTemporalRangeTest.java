package online.anhht.airline.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Route and Temporal Lifecycle Unit Tests")
class RouteAndTemporalRangeTest {

    @Test
    @DisplayName("Should validate route frequency, temporal validity range, and aircraft qualification")
    void testRouteOperations() {
        Airport dep = Airport.of("JFK", "JFK Airport", "New York", "USA", new Coordinates(40.6413, -73.7781), ZoneId.of("America/New_York"), 50);
        Airport arr = Airport.of("LHR", "Heathrow Airport", "London", "UK", new Coordinates(51.4700, -0.4543), ZoneId.of("Europe/London"), 50);

        OffsetDateTime validFrom = OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime validTo = OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC);
        TemporalValidityRange validityRange = new TemporalValidityRange(validFrom, validTo);

        Route route = Route.of(
                "PG0001",
                dep,
                arr,
                Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                LocalTime.of(10, 30),
                Duration.ofHours(7),
                validityRange
        );

        assertEquals("PG0001", route.getFlightNo());
        assertTrue(route.getDistanceKm() > 5500);

        // Operational check
        OffsetDateTime mondayInValidity = OffsetDateTime.of(2026, 6, 1, 10, 30, 0, 0, ZoneOffset.UTC); // 2026-06-01 is Monday
        OffsetDateTime sundayInValidity = OffsetDateTime.of(2026, 6, 7, 10, 30, 0, 0, ZoneOffset.UTC); // Sunday
        OffsetDateTime mondayOutOfRange = OffsetDateTime.of(2027, 6, 7, 10, 30, 0, 0, ZoneOffset.UTC);

        assertTrue(route.operatesOn(DayOfWeek.MONDAY, mondayInValidity));
        assertFalse(route.operatesOn(DayOfWeek.SUNDAY, sundayInValidity));
        assertFalse(route.operatesOn(DayOfWeek.MONDAY, mondayOutOfRange));

        // Aircraft qualification
        Seat seat = Seat.of("1A", FareCondition.ECONOMY, "773");
        CabinLayout layout = CabinLayout.of("773", List.of(seat));
        Airplane longRangePlane = Airplane.of("PL-LR", "Boeing 777-300", 11000, 900, layout);
        Airplane shortRangePlane = Airplane.of("PL-SR", "Sukhoi Superjet", 3000, 800, layout);

        assertTrue(route.isQualifiedAircraft(longRangePlane));
        assertFalse(route.isQualifiedAircraft(shortRangePlane));
    }
}
