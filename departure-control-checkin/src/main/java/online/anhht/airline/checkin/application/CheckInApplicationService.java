package online.anhht.airline.checkin.application;

import online.anhht.airline.checkin.CheckInCoordinator;
import online.anhht.airline.checkin.port.inbound.CheckInUseCase;
import online.anhht.airline.checkin.port.outbound.BoardingPassRepositoryPort;
import online.anhht.airline.checkin.port.outbound.CheckInEventPublisherPort;
import online.anhht.airline.events.BoardingPassIssuedEvent;
import online.anhht.airline.events.PassengerCheckedInEvent;
import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Ticket;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CheckInApplicationService implements CheckInUseCase {

    private final CheckInCoordinator checkInCoordinator = CheckInCoordinator.of();
    private final BoardingPassRepositoryPort boardingPassRepo;
    private final CheckInEventPublisherPort eventPublisher;

    public CheckInApplicationService(BoardingPassRepositoryPort boardingPassRepo, CheckInEventPublisherPort eventPublisher) {
        this.boardingPassRepo = boardingPassRepo;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean isCheckInOpen(Flight flight, OffsetDateTime currentTime) {
        return checkInCoordinator.isCheckInOpen(flight, currentTime);
    }

    @Override
    public BoardingPass checkIn(Ticket ticket, Flight flight, String requestedSeatNo, OffsetDateTime currentTime) {
        BoardingPass pass = checkInCoordinator.checkInSegment(ticket, flight, requestedSeatNo, currentTime);
        boardingPassRepo.save(pass);

        eventPublisher.publish(new BoardingPassIssuedEvent(
            pass.getTicketNo(), pass.getFlightId(), pass.getBoardingNo(), pass.getSeatNo()
        ));
        eventPublisher.publish(new PassengerCheckedInEvent(
            ticket.getTicketNo(), ticket.getPassengerName(), List.of(flight.getFlightId())
        ));

        return pass;
    }

    @Override
    public List<BoardingPass> throughCheckIn(
            Ticket ticket,
            Map<String, Flight> segmentFlightsMap,
            Map<String, String> segmentSeatPreferences,
            OffsetDateTime currentTime
    ) {
        List<BoardingPass> passes = checkInCoordinator.throughCheckIn(
                ticket, segmentFlightsMap, segmentSeatPreferences, currentTime
        );
        boardingPassRepo.saveAll(passes);

        List<String> flightIds = passes.stream().map(BoardingPass::getFlightId).toList();
        eventPublisher.publish(new PassengerCheckedInEvent(ticket.getTicketNo(), ticket.getPassengerName(), flightIds));

        for (BoardingPass pass : passes) {
            eventPublisher.publish(new BoardingPassIssuedEvent(
                    pass.getTicketNo(), pass.getFlightId(), pass.getBoardingNo(), pass.getSeatNo()
            ));
        }

        return passes;
    }

    @Override
    public List<BoardingPass> getBoardingPassesForFlight(String flightId) {
        return boardingPassRepo.findByFlightId(flightId);
    }

    @Override
    public Optional<BoardingPass> getBoardingPass(String ticketNo, String flightId) {
        return boardingPassRepo.findByTicketNoAndFlightId(ticketNo, flightId);
    }
}
