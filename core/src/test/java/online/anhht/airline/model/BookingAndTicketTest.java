package online.anhht.airline.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sales, Booking & Ticketing Domain Unit Tests")
class BookingAndTicketTest {

    @Test
    @DisplayName("Should construct multi-leg ticket with dynamic passenger identification and calculate totals")
    void testMultiLegTicketAndGroupBooking() {
        TicketFlightSegment outboundLeg1 = TicketFlightSegment.of("FL-101", FareCondition.ECONOMY, new BigDecimal("150.00"), true);
        TicketFlightSegment outboundLeg2 = TicketFlightSegment.of("FL-102", FareCondition.ECONOMY, new BigDecimal("200.00"), true);
        TicketFlightSegment returnLeg = TicketFlightSegment.of("FL-103", FareCondition.ECONOMY, new BigDecimal("320.00"), false);

        // Passenger 1
        Ticket ticket1 = Ticket.of(
                "0005432000001",
                "BK001A",
                "PASS-987654",
                "ALEXANDER PETROV",
                "{\"email\":\"alex@example.com\"}",
                List.of(outboundLeg1, outboundLeg2, returnLeg)
        );

        // Passenger 2 (Companion in group booking)
        Ticket ticket2 = Ticket.of(
                "0005432000002",
                "BK001A",
                "PASS-987655",
                "ELENA PETROVA",
                "{\"email\":\"elena@example.com\"}",
                List.of(outboundLeg1, outboundLeg2, returnLeg)
        );

        assertEquals(new BigDecimal("670.00"), ticket1.getTotalAmount());
        assertTrue(ticket1.isRoundTrip());
        assertEquals(3, ticket1.getSegmentCount());
        assertEquals("ALEXANDER PETROV", ticket1.getPassengerName());
        assertEquals("PASS-987654", ticket1.getPassengerId());

        // Group Booking
        Booking booking = Booking.of("BK001A", OffsetDateTime.of(2026, 6, 1, 10, 0, 0, 0, ZoneOffset.UTC), List.of(ticket1, ticket2));

        assertEquals("BK001A", booking.getBookRef());
        assertEquals(2, booking.getPassengerCount());
        assertEquals(new BigDecimal("1340.00"), booking.getTotalAmount());
    }

    @Test
    @DisplayName("Should reject mismatched booking references on tickets")
    void testBookingReferenceMismatch() {
        TicketFlightSegment seg = TicketFlightSegment.of("FL-101", FareCondition.BUSINESS, new BigDecimal("500.00"), true);
        Ticket ticket = Ticket.of("0005432000003", "BK999Z", "PASS-111", "JOHN DOE", null, List.of(seg));

        Booking booking = Booking.of("BK001A", OffsetDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> booking.addTicket(ticket));
    }
}
