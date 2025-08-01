package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * Initial state when a flight is generated from schedule.
 */
public class ScheduledFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.SCHEDULED;
    }

    @Override
    public void markOnTime(Flight flight) {
        flight.changeState(new OnTimeFlightState());
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
        throw new IllegalStateException("Flight " + flight.getFlightId() + " cannot depart directly from SCHEDULED state without boarding.");
    }

    @Override
    public void arrive(Flight flight, OffsetDateTime actualArrival) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " cannot arrive while in SCHEDULED state.");
    }

    @Override
    public void cancel(Flight flight, String reason) {
        flight.setCancelReason(reason);
        flight.changeState(new CancelledFlightState());
    }
}
