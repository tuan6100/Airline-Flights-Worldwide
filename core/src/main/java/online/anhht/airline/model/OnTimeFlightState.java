package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * State representing a flight confirmed to be on time.
 */
public class OnTimeFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.ON_TIME;
    }

    @Override
    public void markOnTime(Flight flight) {
        // already on time
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        flight.setEstimatedDeparture(expectedDeparture);
        flight.setDelayReason(reason);
        flight.changeState(new DelayedFlightState());
    }

    @Override
    public void startBoarding(Flight flight) {
        flight.changeState(new BoardingFlightState());
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " cannot depart before boarding.");
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
