package online.anhht.airline.checkin.adapter.outbound;

import online.anhht.airline.checkin.port.outbound.BoardingPassRepositoryPort;
import online.anhht.airline.model.BoardingPass;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBoardingPassRepository implements BoardingPassRepositoryPort {

    // Map: flightId -> Map(ticketNo -> BoardingPass)
    private final Map<String, Map<String, BoardingPass>> store = new ConcurrentHashMap<>();

    @Override
    public List<BoardingPass> findByFlightId(String flightId) {
        Map<String, BoardingPass> map = store.get(flightId);
        if (map == null) return List.of();
        return new ArrayList<>(map.values());
    }

    @Override
    public Optional<BoardingPass> findByTicketNoAndFlightId(String ticketNo, String flightId) {
        Map<String, BoardingPass> map = store.get(flightId);
        if (map == null) return Optional.empty();
        return Optional.ofNullable(map.get(ticketNo));
    }

    @Override
    public void save(BoardingPass boardingPass) {
        store.computeIfAbsent(boardingPass.getFlightId(), k -> new ConcurrentHashMap<>())
                .put(boardingPass.getTicketNo(), boardingPass);
    }

    @Override
    public void saveAll(List<BoardingPass> boardingPasses) {
        for (BoardingPass bp : boardingPasses) {
            save(bp);
        }
    }
}
