package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * State representing an active delay.
 */
public class DelayedFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.DELAYED;
    }

    @Override
    public void markOnTime(Flight flight) {
        flight.setEstimatedDeparture(null);
        flight.setDelayReason(null);
        flight.changeState(new OnTimeFlightState());
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        flight.setEstimatedDeparture(expectedDeparture);
        flight.setDelayReason(reason);
    }

    @Override
    public void startBoarding(Flight flight) {
        flight.changeState(new BoardingFlightState());
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " cannot depart directly without boarding.");
    }

    @Override
    public void arrive(Flight flight, OffsetDateTime actualArrival) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " cannot arrive before departure.");
    }

    @Override
    public void cancel(Flight flight, String reason) {
        flight.setCancelReason(reason);
        flight.changeState(new CancelledFlightState());
    }
}
