package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * State representing active passenger boarding.
 */
public class BoardingFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.BOARDING;
    }

    @Override
    public void markOnTime(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " is currently BOARDING.");
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        flight.setEstimatedDeparture(expectedDeparture);
        flight.setDelayReason(reason);
        flight.changeState(new DelayedFlightState());
    }

    @Override
    public void startBoarding(Flight flight) {
        // already boarding
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        flight.setActualDeparture(actualDeparture);
        flight.changeState(new DepartedFlightState());
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
