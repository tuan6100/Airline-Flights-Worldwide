package online.anhht.airline.operations.adapter.outbound.jpa;

import online.anhht.airline.model.Flight;
import online.anhht.airline.operations.adapter.outbound.InMemoryFlightRepository;
import online.anhht.airline.operations.port.outbound.FlightRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaFlightRepositoryAdapter implements FlightRepositoryPort {

    private final SpringDataFlightRepository springDataRepository;
    private final InMemoryFlightRepository inMemoryFallback;

    public JpaFlightRepositoryAdapter(SpringDataFlightRepository springDataRepository, InMemoryFlightRepository inMemoryFallback) {
        this.springDataRepository = springDataRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<Flight> findAll() {
        try {
            List<FlightEntity> entities = springDataRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream().map(FlightEntity::toDomain).toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Flight> findById(String flightId) {
        if (flightId == null) return Optional.empty();
        try {
            Optional<FlightEntity> entity = springDataRepository.findById(flightId);
            if (entity.isPresent()) {
                return entity.map(FlightEntity::toDomain);
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findById(flightId);
    }

    @Override
    public void save(Flight flight) {
        try {
            springDataRepository.save(FlightEntity.fromDomain(flight));
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(flight);
    }

    @Override
    public void saveAll(List<Flight> flights) {
        for (Flight f : flights) {
            save(f);
        }
    }
}
