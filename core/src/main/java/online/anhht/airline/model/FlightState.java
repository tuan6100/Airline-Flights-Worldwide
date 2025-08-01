package online.anhht.airline.model;

import java.time.OffsetDateTime;

/**
 * State interface for the Flight state machine.
 */
public interface FlightState {
    FlightStatus getStatus();
    void markOnTime(Flight flight);
    void markDelayed(Flight flight, OffsetDateTime expectedDeparture, String reason);
    void startBoarding(Flight flight);
    void depart(Flight flight, OffsetDateTime actualDeparture);
    void arrive(Flight flight, OffsetDateTime actualArrival);
    void cancel(Flight flight, String reason);
}
