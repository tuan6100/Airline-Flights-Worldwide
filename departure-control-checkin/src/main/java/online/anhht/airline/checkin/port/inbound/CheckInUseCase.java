package online.anhht.airline.checkin.port.inbound;

import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Ticket;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CheckInUseCase {

    boolean isCheckInOpen(Flight flight, OffsetDateTime currentTime);

    BoardingPass checkIn(Ticket ticket, Flight flight, String requestedSeatNo, OffsetDateTime currentTime);

    List<BoardingPass> throughCheckIn(
            Ticket ticket,
            Map<String, Flight> segmentFlightsMap,
            Map<String, String> segmentSeatPreferences,
            OffsetDateTime currentTime
    );

    List<BoardingPass> getBoardingPassesForFlight(String flightId);

    Optional<BoardingPass> getBoardingPass(String ticketNo, String flightId);
}
