package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * State representing an airborne flight that has departed.
 */
public class DepartedFlightState implements FlightState {

    @Override
    public FlightStatus getStatus() {
        return FlightStatus.DEPARTED;
    }

    @Override
    public void markOnTime(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already departed.");
    }

    @Override
    public void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already departed.");
    }

    @Override
    public void startBoarding(Flight flight) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already departed.");
    }

    @Override
    public void depart(Flight flight, OffsetDateTime actualDeparture) {
        // already departed
    }

    @Override
    public void arrive(Flight flight, OffsetDateTime actualArrival) {
        flight.setActualArrival(actualArrival);
        flight.changeState(new ArrivedFlightState());
    }

    @Override
    public void cancel(Flight flight, String reason) {
        throw new IllegalStateException("Flight " + flight.getFlightId() + " has already departed and cannot be cancelled mid-flight.");
    }
}
