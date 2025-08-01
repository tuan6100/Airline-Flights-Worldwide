package online.anhht.airline.checkin;

import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.FlightStatus;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default thread-safe implementation of {@link CheckInCoordinator}.
 */
public class DefaultCheckInCoordinator implements CheckInCoordinator {

    // Map: flightId -> Set of assigned seat numbers (thread-safe uniqueness)
    private final Map<String, Set<String>> flightSeatAllocations = new ConcurrentHashMap<>();

    // Map: flightId -> boarding sequence counter
    private final Map<String, AtomicInteger> flightBoardingCounters = new ConcurrentHashMap<>();

    // Map: flightId -> Map(ticketNo -> BoardingPass)
    private final Map<String, Map<String, BoardingPass>> issuedBoardingPasses = new ConcurrentHashMap<>();

    @Override
    public boolean isCheckInOpen(@NonNull Flight flight, @NonNull OffsetDateTime currentTime) {
        Objects.requireNonNull(flight, "Flight must not be null");
        Objects.requireNonNull(currentTime, "Current time must not be null");

        if (flight.getStatus() == FlightStatus.CANCELLED ||
            flight.getStatus() == FlightStatus.DEPARTED ||
            flight.getStatus() == FlightStatus.ARRIVED) {
            return false;
        }

        OffsetDateTime windowOpen = flight.getScheduledDeparture().minusHours(24);
        OffsetDateTime windowClose = flight.getScheduledDeparture();

        return !currentTime.isBefore(windowOpen) && !currentTime.isAfter(windowClose);
    }

    @Override
    public BoardingPass checkInSegment(
            @NonNull Ticket ticket,
            @NonNull Flight flight,
            @NonNull String requestedSeatNo,
            @NonNull OffsetDateTime currentTime
    ) {
        Objects.requireNonNull(ticket, "Ticket must not be null");
        Objects.requireNonNull(flight, "Flight must not be null");
        Objects.requireNonNull(requestedSeatNo, "Seat number must not be null");

        if (!isCheckInOpen(flight, currentTime)) {
            throw new IllegalStateException(String.format(
                    "Check-in window is not open for flight %s at %s. Scheduled departure: %s",
                    flight.getFlightId(), currentTime, flight.getScheduledDeparture()
            ));
        }

        String normalizedSeat = requestedSeatNo.trim().toUpperCase();
        if (!flight.getAirplane().getCabinLayout().hasSeat(normalizedSeat)) {
            throw new IllegalArgumentException(String.format(
                    "Seat %s does not exist on aircraft %s",
                    normalizedSeat, flight.getAirplane().getModel()
            ));
        }

        Set<String> allocatedSeats = flightSeatAllocations.computeIfAbsent(
                flight.getFlightId(), k -> ConcurrentHashMap.newKeySet()
        );

        // Atomic seat reservation
        if (!allocatedSeats.add(normalizedSeat)) {
            throw new IllegalStateException(String.format(
                    "Seat %s is already allocated for flight %s (No double-booking allowed)",
                    normalizedSeat, flight.getFlightId()
            ));
        }

        int boardingSeq = flightBoardingCounters.computeIfAbsent(
                flight.getFlightId(), k -> new AtomicInteger(0)
        ).incrementAndGet();

        BoardingPass boardingPass = BoardingPass.of(
                ticket.getTicketNo(), flight.getFlightId(), boardingSeq, normalizedSeat, currentTime
        );

        issuedBoardingPasses.computeIfAbsent(flight.getFlightId(), k -> new ConcurrentHashMap<>())
                .put(ticket.getTicketNo(), boardingPass);

        return boardingPass;
    }

    @Override
    public List<BoardingPass> throughCheckIn(
            @NonNull Ticket ticket,
            @NonNull Map<String, Flight> segmentFlightsMap,
            @NonNull Map<String, String> segmentSeatPreferences,
            @NonNull OffsetDateTime currentTime
    ) {
        Objects.requireNonNull(ticket, "Ticket must not be null");
        List<BoardingPass> passes = new ArrayList<>();

        for (TicketFlightSegment segment : ticket.getSegments()) {
            Flight flight = segmentFlightsMap.get(segment.getFlightId());
            if (flight == null) {
                throw new IllegalArgumentException("Missing flight instance for segment flight ID: " + segment.getFlightId());
            }
            String seat = segmentSeatPreferences.get(segment.getFlightId());
            if (seat == null) {
                // Auto-pick first available seat
                seat = findFirstAvailableSeat(flight);
            }
            BoardingPass pass = checkInSegment(ticket, flight, seat, currentTime);
            passes.add(pass);
        }

        return Collections.unmodifiableList(passes);
    }

    private String findFirstAvailableSeat(Flight flight) {
        Set<String> allocated = flightSeatAllocations.getOrDefault(flight.getFlightId(), Set.of());
        return flight.getAirplane().getCabinLayout().getSeats().stream()
                .map(s -> s.getSeatNo())
                .filter(s -> !allocated.contains(s))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Flight " + flight.getFlightId() + " is fully booked."));
    }

    @Override
    public boolean isSeatAllocated(String flightId, String seatNo) {
        Set<String> allocated = flightSeatAllocations.get(flightId);
        return allocated != null && allocated.contains(seatNo.trim().toUpperCase());
    }

    @Override
    public List<BoardingPass> getIssuedBoardingPassesForFlight(String flightId) {
        Map<String, BoardingPass> map = issuedBoardingPasses.get(flightId);
        if (map == null) return List.of();
        return new ArrayList<>(map.values());
    }
}
