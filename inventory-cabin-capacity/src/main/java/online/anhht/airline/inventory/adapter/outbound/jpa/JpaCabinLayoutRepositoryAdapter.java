package online.anhht.airline.inventory.adapter.outbound.jpa;

import online.anhht.airline.inventory.adapter.outbound.InMemoryCabinLayoutRepository;
import online.anhht.airline.inventory.port.outbound.CabinLayoutRepositoryPort;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Seat;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaCabinLayoutRepositoryAdapter implements CabinLayoutRepositoryPort {

    private final SpringDataSeatRepository springDataSeatRepository;
    private final InMemoryCabinLayoutRepository inMemoryFallback;

    public JpaCabinLayoutRepositoryAdapter(SpringDataSeatRepository springDataSeatRepository, InMemoryCabinLayoutRepository inMemoryFallback) {
        this.springDataSeatRepository = springDataSeatRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<CabinLayout> findAll() {
        try {
            List<SeatEntity> allSeats = springDataSeatRepository.findAll();
            if (!allSeats.isEmpty()) {
                return allSeats.stream()
                        .map(SeatEntity::getAirplaneCode)
                        .distinct()
                        .map(this::findByAircraftCode)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<CabinLayout> findByAircraftCode(String aircraftCode) {
        if (aircraftCode == null) return Optional.empty();
        try {
            List<SeatEntity> seats = springDataSeatRepository.findByAirplaneCode(aircraftCode.trim().toUpperCase());
            if (!seats.isEmpty()) {
                List<Seat> domainSeats = seats.stream().map(SeatEntity::toDomain).toList();
                return Optional.of(CabinLayout.of(aircraftCode, domainSeats));
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByAircraftCode(aircraftCode);
    }

    @Override
    public void save(CabinLayout cabinLayout) {
        try {
            List<SeatEntity> entities = cabinLayout.getSeats().stream()
                    .map(SeatEntity::fromDomain)
                    .toList();
            springDataSeatRepository.saveAll(entities);
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(cabinLayout);
    }
}
