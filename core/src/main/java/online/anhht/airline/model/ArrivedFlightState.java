package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * Terminal state representing a successfully completed and arrived flight.
 */
public class ArrivedFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.ARRIVED;
    }

    @Override
    public void markOnTime(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already arrived.");
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already arrived.");
    }

    @Override
    public void startBoarding(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already arrived.");
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already arrived.");
    }

    @Override
    public void arrive(Flight flight, OffsetDateTime actualArrival) {
        // already arrived
    }

    @Override
    public void cancel(Flight flight, String reason) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already arrived.");
    }
}
