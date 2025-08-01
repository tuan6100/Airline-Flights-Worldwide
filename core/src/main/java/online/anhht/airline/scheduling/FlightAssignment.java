package online.anhht.airline.scheduling;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Route;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Represents the assignment of an aircraft to a scheduled route at a particular departure time.
 */
public record FlightAssignment(
        String flightId,
        Route route,
        Airplane airplane,
        OffsetDateTime scheduledDeparture,
        OffsetDateTime scheduledArrival
) {
    public FlightAssignment {
        Objects.requireNonNull(flightId, "flightId must not be null");
        Objects.requireNonNull(route, "route must not be null");
        Objects.requireNonNull(airplane, "airplane must not be null");
        Objects.requireNonNull(scheduledDeparture, "scheduledDeparture must not be null");
        Objects.requireNonNull(scheduledArrival, "scheduledArrival must not be null");
    }

    public boolean overlapsWith(FlightAssignment other) {
        if (!this.airplane.equals(other.airplane)) {
            return false;
        }
        return !this.scheduledArrival.isBefore(other.scheduledDeparture) &&
                !other.scheduledArrival.isBefore(this.scheduledDeparture);
    }
}
