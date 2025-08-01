package online.anhht.airline.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cabin Layout and Seat Domain Unit Tests")
class CabinLayoutAndSeatTest {

    @Test
    @DisplayName("Should correctly calculate cabin capacities by fare condition")
    void testCabinLayoutCapacities() {
        Seat s1 = Seat.of("1A", FareCondition.BUSINESS, "773");
        Seat s2 = Seat.of("1B", FareCondition.BUSINESS, "773");
        Seat s3 = Seat.of("2A", FareCondition.COMFORT, "773");
        Seat s4 = Seat.of("3A", FareCondition.ECONOMY, "773");
        Seat s5 = Seat.of("3B", FareCondition.ECONOMY, "773");
        Seat s6 = Seat.of("3C", FareCondition.ECONOMY, "773");

        CabinLayout layout = CabinLayout.of("773", List.of(s1, s2, s3, s4, s5, s6));

        assertEquals(6, layout.getTotalCapacity());
        assertEquals(2, layout.getCapacity(FareCondition.BUSINESS));
        assertEquals(1, layout.getCapacity(FareCondition.COMFORT));
        assertEquals(3, layout.getCapacity(FareCondition.ECONOMY));

        assertTrue(layout.hasSeat("1A"));
        assertTrue(layout.hasSeat("3c")); // case-insensitive check
        assertFalse(layout.hasSeat("99Z"));
    }
}
