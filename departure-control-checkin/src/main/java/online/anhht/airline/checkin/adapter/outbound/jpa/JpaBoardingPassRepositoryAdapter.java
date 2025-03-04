package online.anhht.airline.checkin.adapter.outbound.jpa;

import online.anhht.airline.checkin.adapter.outbound.InMemoryBoardingPassRepository;
import online.anhht.airline.checkin.port.outbound.BoardingPassRepositoryPort;
import online.anhht.airline.model.BoardingPass;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaBoardingPassRepositoryAdapter implements BoardingPassRepositoryPort {

    private final SpringDataBoardingPassRepository springDataRepository;
    private final InMemoryBoardingPassRepository inMemoryFallback;

    public JpaBoardingPassRepositoryAdapter(SpringDataBoardingPassRepository springDataRepository, InMemoryBoardingPassRepository inMemoryFallback) {
        this.springDataRepository = springDataRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<BoardingPass> findByFlightId(String flightId) {
        if (flightId == null) return List.of();
        try {
            List<BoardingPassEntity> entities = springDataRepository.findByFlightId(flightId);
            if (!entities.isEmpty()) {
                return entities.stream().map(BoardingPassEntity::toDomain).toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByFlightId(flightId);
    }

    @Override
    public Optional<BoardingPass> findByTicketNoAndFlightId(String ticketNo, String flightId) {
        if (ticketNo == null || flightId == null) return Optional.empty();
        try {
            Optional<BoardingPassEntity> entity = springDataRepository.findById(new BoardingPassId(ticketNo, flightId));
            if (entity.isPresent()) {
                return entity.map(BoardingPassEntity::toDomain);
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByTicketNoAndFlightId(ticketNo, flightId);
    }

    @Override
    public void save(BoardingPass boardingPass) {
        try {
            springDataRepository.save(BoardingPassEntity.fromDomain(boardingPass));
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(boardingPass);
    }

    @Override
    public void saveAll(List<BoardingPass> boardingPasses) {
        for (BoardingPass bp : boardingPasses) {
            save(bp);
        }
    }
}
