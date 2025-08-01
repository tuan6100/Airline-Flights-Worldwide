package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * Terminal state representing a cancelled flight.
 */
public class CancelledFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.CANCELLED;
    }

    @Override
    public void markOnTime(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is CANCELLED.");
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is CANCELLED.");
    }

    @Override
    public void startBoarding(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is CANCELLED.");
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is CANCELLED.");
    }

    @Override
    public void arrive(Flight flight, OffsetDateTime actualArrival) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is CANCELLED.");
    }

    @Override
    public void cancel(Flight flight, String reason) {
        // already cancelled
    }
}
