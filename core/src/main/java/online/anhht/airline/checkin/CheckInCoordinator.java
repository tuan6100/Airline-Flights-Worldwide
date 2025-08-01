package online.anhht.airline.checkin;

import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Ticket;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Departure Control & Check-In Domain coordinator enforcing:
 * 1. 24-hour check-in window.
 * 2. Through check-in across all legs of a ticket.
 * 3. Atomic seat allocation and no-double-booking constraint on (flight_id, seat_no).
 * 4. Sequential boarding sequence number per flight.
 */
public interface CheckInCoordinator {

    /**
     * Checks whether check-in is currently open for a flight (24h before departure up to departure).
     */
    boolean isCheckInOpen(@NonNull Flight flight, @NonNull OffsetDateTime currentTime);

    /**
     * Performs atomic check-in and seat allocation for a specific segment.
     */
    BoardingPass checkInSegment(
            @NonNull Ticket ticket,
            @NonNull Flight flight,
            @NonNull String requestedSeatNo,
            @NonNull OffsetDateTime currentTime
    );

    /**
     * Performs through check-in for all segments in a ticket where flights are provided.
     */
    List<BoardingPass> throughCheckIn(
            @NonNull Ticket ticket,
            @NonNull Map<String, Flight> segmentFlightsMap,
            @NonNull Map<String, String> segmentSeatPreferences,
            @NonNull OffsetDateTime currentTime
    );

    /**
     * Checks if a seat has already been allocated for the specified flight.
     */
    boolean isSeatAllocated(String flightId, String seatNo);

    /**
     * Returns all issued boarding passes for the specified flight.
     */
    List<BoardingPass> getIssuedBoardingPassesForFlight(String flightId);

    /**
     * Creates a new instance of {@link CheckInCoordinator}.
     */
    static CheckInCoordinator of() {
        return new DefaultCheckInCoordinator();
    }
}
