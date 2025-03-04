package online.anhht.airline.checkin.port.outbound;

import online.anhht.airline.model.BoardingPass;

import java.util.List;
import java.util.Optional;

public interface BoardingPassRepositoryPort {
    List<BoardingPass> findByFlightId(String flightId);
    Optional<BoardingPass> findByTicketNoAndFlightId(String ticketNo, String flightId);
    void save(BoardingPass boardingPass);
    void saveAll(List<BoardingPass> boardingPasses);
}
