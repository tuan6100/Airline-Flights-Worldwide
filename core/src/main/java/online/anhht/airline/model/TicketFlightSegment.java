package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Public class representing a Ticket Flight Segment itinerary leg.
 */
public class TicketFlightSegment {

    @Getter
    private final String flightId;

    @Getter
    private final FareCondition fareCondition;

    @Getter
    private final BigDecimal price;

    @Getter
    private final boolean outbound;

    public TicketFlightSegment(
            @NonNull String flightId,
            @NonNull FareCondition fareCondition,
            @NonNull BigDecimal price,
            boolean outbound
    ) {
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Segment price cannot be negative: " + price);
        }
        this.flightId = flightId;
        this.fareCondition = fareCondition;
        this.price = price;
        this.outbound = outbound;
    }

    public static TicketFlightSegment of(
            @NonNull String flightId,
            @NonNull FareCondition fareCondition,
            @NonNull BigDecimal price,
            boolean outbound
    ) {
        return new TicketFlightSegment(flightId, fareCondition, price, outbound);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TicketFlightSegment that)) return false;
        return outbound == that.isOutbound() &&
                Objects.equals(flightId, that.getFlightId()) &&
                fareCondition == that.getFareCondition() &&
                Objects.equals(price, that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId, fareCondition, price, outbound);
    }

    @Override
    public String toString() {
        return String.format("Segment[Flight: %s, Class: %s, Price: %s, Outbound: %b]",
                flightId, fareCondition, price, outbound);
    }
}
