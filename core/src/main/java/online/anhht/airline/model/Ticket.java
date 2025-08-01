package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Public class representing a passenger Ticket covering one or more flight segments.
 */
public class Ticket {

    @Getter
    private final String ticketNo;

    @Getter
    private final String bookRef;

    @Getter
    private final String passengerId;

    @Getter
    private final String passengerName;

    @Getter
    private final String contactData;

    @Getter
    private final List<TicketFlightSegment> segments;

    public Ticket(
            @NonNull String ticketNo,
            @NonNull String bookRef,
            @NonNull String passengerId,
            @NonNull String passengerName,
            String contactData,
            @NonNull List<TicketFlightSegment> segments
    ) {
        if (ticketNo.length() != 13) {
            throw new IllegalArgumentException("Ticket number must be 13 characters: " + ticketNo);
        }
        if (bookRef.length() != 6) {
            throw new IllegalArgumentException("Booking reference must be 6 characters: " + bookRef);
        }
        if (segments.isEmpty()) {
            throw new IllegalArgumentException("Ticket must contain at least one flight segment.");
        }
        this.ticketNo = ticketNo;
        this.bookRef = bookRef.toUpperCase();
        this.passengerId = passengerId.trim();
        this.passengerName = passengerName.trim().toUpperCase();
        this.contactData = contactData != null ? contactData : "{}";
        this.segments = List.copyOf(segments);
    }

    public BigDecimal getTotalAmount() {
        return segments.stream()
                .map(TicketFlightSegment::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isRoundTrip() {
        boolean hasOutbound = segments.stream().anyMatch(TicketFlightSegment::isOutbound);
        boolean hasInbound = segments.stream().anyMatch(s -> !s.isOutbound());
        return hasOutbound && hasInbound;
    }

    public int getSegmentCount() {
        return segments.size();
    }

    public static Ticket of(
            @NonNull String ticketNo,
            @NonNull String bookRef,
            @NonNull String passengerId,
            @NonNull String passengerName,
            String contactData,
            @NonNull List<TicketFlightSegment> segments
    ) {
        return new Ticket(ticketNo, bookRef, passengerId, passengerName, contactData, segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ticket ticket)) return false;
        return Objects.equals(ticketNo, ticket.getTicketNo());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketNo);
    }

    @Override
    public String toString() {
        return String.format("Ticket[%s, BookRef: %s, Pax: %s (%s), Segments: %d, Total: %s]",
                ticketNo, bookRef, passengerName, passengerId, segments.size(), getTotalAmount());
    }
}
