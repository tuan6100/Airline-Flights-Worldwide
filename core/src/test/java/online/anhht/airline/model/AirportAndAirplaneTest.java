package online.anhht.airline.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Airport and Airplane Core Domain Unit Tests")
class AirportAndAirplaneTest {

    private Airplane createSampleAirplane(String id, int rangeKm) {
        Seat s1 = Seat.of("1A", FareCondition.BUSINESS, id);
        Seat s2 = Seat.of("2A", FareCondition.ECONOMY, id);
        CabinLayout layout = CabinLayout.of(id, List.of(s1, s2));
        return Airplane.of(id, "Boeing 777-300", rangeKm, 900, layout);
    }

    @Test
    @DisplayName("Should create airport and calculate distance accurately")
    void testAirportCreationAndDistance() {
        Coordinates jfkCoord = new Coordinates(40.6413, -73.7781);
        Airport jfk = Airport.of("JFK", "John F Kennedy", "New York", "USA", jfkCoord, ZoneId.of("America/New_York"), 10);

        Coordinates lhrCoord = new Coordinates(51.4700, -0.4543);
        Airport lhr = Airport.of("LHR", "Heathrow", "London", "UK", lhrCoord, ZoneId.of("Europe/London"), 10);

        assertEquals("JFK", jfk.getAirportCode());
        assertEquals("LHR", lhr.getAirportCode());

        double distance = jfk.distanceTo(lhr);
        assertTrue(distance > 5500 && distance < 5600, "Distance should be ~5555 km, was: " + distance);
    }

    @Test
    @DisplayName("Should enforce airport capacity constraints")
    void testAirportCapacityConstraints() {
        Airport airport = Airport.of("SVO", 2);
        Airplane plane1 = createSampleAirplane("PL01", 8000);
        Airplane plane2 = createSampleAirplane("PL02", 8000);
        Airplane plane3 = createSampleAirplane("PL03", 8000);

        airport.addAirplane(plane1);
        airport.addAirplane(plane2);
        assertEquals(2, airport.getParkedAirplaneCount());
        assertFalse(airport.hasCapacity());

        assertThrows(IllegalStateException.class, () -> airport.addAirplane(plane3));

        airport.removeAirplane(plane1);
        assertEquals(1, airport.getParkedAirplaneCount());
        assertTrue(airport.hasCapacity());
    }

    @Test
    @DisplayName("Should correctly manage Airplane state transitions")
    void testAirplaneStateTransitions() {
        Airplane plane = createSampleAirplane("PL01", 8000);
        Airport dep = Airport.of("JFK", 5);
        Airport arr = Airport.of("LHR", 5);

        plane.parkAt(dep);
        assertEquals("PARKED", plane.getState().getStateName());
        assertTrue(dep.isAirplaneParked("PL01"));

        plane.takeOff(dep);
        assertEquals("IN_FLIGHT", plane.getState().getStateName());
        assertFalse(dep.isAirplaneParked("PL01"));

        // Cannot take off again while in flight
        assertThrows(IllegalStateException.class, () -> plane.takeOff(dep));

        plane.land(arr);
        assertEquals("PARKED", plane.getState().getStateName());
        assertTrue(arr.isAirplaneParked("PL01"));

        // Maintenance state
        plane.enterMaintenance();
        assertEquals("MAINTENANCE", plane.getState().getStateName());
        assertThrows(IllegalStateException.class, () -> plane.takeOff(arr));

        plane.exitMaintenance();
        assertEquals("PARKED", plane.getState().getStateName());
    }

    @Test
    @DisplayName("Should evaluate airplane range qualification")
    void testAirplaneRangeQualification() {
        Airplane shortRange = createSampleAirplane("SR01", 3000);
        Airplane longRange = createSampleAirplane("LR01", 12000);

        assertTrue(shortRange.isRangeQualified(2500));
        assertFalse(shortRange.isRangeQualified(3500));
        assertTrue(longRange.isRangeQualified(10000));
    }
}
