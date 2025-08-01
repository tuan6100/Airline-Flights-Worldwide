package online.anhht.airline.planning;

import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.TemporalValidityRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight Route Planner & Network Query Engine Unit Tests")
class FlightRoutePlannerTest {

    private FlightRoutePlanner planner;
    private Airport svo;
    private Airport led;
    private Airport aer;
    private Airport kzn;
    private Airport jfk;
    private Airport lhr;

    @BeforeEach
    void setUp() {
        svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.97, 37.41), ZoneId.of("Europe/Moscow"), 100);
        led = Airport.of("LED", "Pulkovo", "Saint Petersburg", "Russia", new Coordinates(59.80, 30.26), ZoneId.of("Europe/Moscow"), 100);
        aer = Airport.of("AER", "Sochi", "Sochi", "Russia", new Coordinates(43.44, 39.95), ZoneId.of("Europe/Moscow"), 100);
        kzn = Airport.of("KZN", "Kazan", "Kazan", "Russia", new Coordinates(55.60, 49.27), ZoneId.of("Europe/Moscow"), 100);
        jfk = Airport.of("JFK", "John F. Kennedy", "New York", "USA", new Coordinates(40.64, -73.77), ZoneId.of("America/New_York"), 100);
        lhr = Airport.of("LHR", "Heathrow", "London", "UK", new Coordinates(51.47, -0.45), ZoneId.of("Europe/London"), 100);

        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route r1 = Route.of("SU001", svo, led, Set.of(DayOfWeek.values()), LocalTime.of(8, 0), Duration.ofHours(1).plusMinutes(30), validity);
        Route r2 = Route.of("SU002", led, aer, Set.of(DayOfWeek.values()), LocalTime.of(11, 0), Duration.ofHours(3), validity);
        Route r3 = Route.of("SU003", svo, kzn, Set.of(DayOfWeek.values()), LocalTime.of(9, 0), Duration.ofHours(1).plusMinutes(20), validity);
        Route r4 = Route.of("SU004", kzn, aer, Set.of(DayOfWeek.values()), LocalTime.of(12, 0), Duration.ofHours(2).plusMinutes(30), validity);
        Route r5 = Route.of("SU100", svo, jfk, Set.of(DayOfWeek.values()), LocalTime.of(14, 0), Duration.ofHours(10), validity);
        Route r6 = Route.of("BA200", lhr, jfk, Set.of(DayOfWeek.values()), LocalTime.of(10, 0), Duration.ofHours(8), validity);

        planner = FlightRoutePlanner.of(
                List.of(svo, led, aer, kzn, jfk, lhr),
                List.of(r1, r2, r3, r4, r5, r6)
        );
    }

    @Test
    @DisplayName("Should find optimal shortest multi-leg itinerary via A* search")
    void testFindShortestItinerary() {
        Optional<FlightItineraryPlan> planOpt = planner.findShortestItinerary("SVO", "AER");
        assertTrue(planOpt.isPresent());

        FlightItineraryPlan plan = planOpt.get();
        assertEquals(2, plan.legs().size());
        assertEquals(1, plan.layoverCount());
        assertEquals("SVO", plan.getOriginAirportCode());
        assertEquals("AER", plan.getDestinationAirportCode());
        assertFalse(plan.isDirect());
        assertTrue(plan.totalDistanceKm() > 0);
    }

    @Test
    @DisplayName("Should find top-K alternative flight itineraries via Yen algorithm")
    void testFindAlternativeItineraries() {
        List<FlightItineraryPlan> alternatives = planner.findAlternativeItineraries("SVO", "AER", 2);
        assertEquals(2, alternatives.size());

        // Both alternatives connect SVO to AER with different transfers (LED vs KZN)
        assertEquals("SVO", alternatives.get(0).getOriginAirportCode());
        assertEquals("AER", alternatives.get(0).getDestinationAirportCode());
        assertEquals("SVO", alternatives.get(1).getOriginAirportCode());
        assertEquals("AER", alternatives.get(1).getDestinationAirportCode());
    }

    @Test
    @DisplayName("Should discover connecting itineraries within maximum stops constraint")
    void testFindItinerariesWithinMaxStops() {
        List<FlightItineraryPlan> plans = planner.findItinerariesWithinMaxStops("SVO", "AER", 1);
        assertEquals(2, plans.size());
        for (FlightItineraryPlan plan : plans) {
            assertTrue(plan.layoverCount() <= 1);
        }
    }

    @Test
    @DisplayName("Should detect hub airports and network reachability")
    void testHubIdentificationAndReachability() {
        List<Airport> hubs = planner.identifyHubAirports(2);
        assertFalse(hubs.isEmpty());

        Set<String> reachableFromSvo = planner.findReachableAirports("SVO");
        assertTrue(reachableFromSvo.containsAll(Set.of("SVO", "LED", "AER", "KZN", "JFK")));

        Map<String, Integer> hops = planner.calculateShortestHopsFrom("SVO");
        assertEquals(0, hops.get("SVO"));
        assertEquals(1, hops.get("LED"));
        assertEquals(1, hops.get("KZN"));
        assertEquals(2, hops.get("AER"));
    }
}
