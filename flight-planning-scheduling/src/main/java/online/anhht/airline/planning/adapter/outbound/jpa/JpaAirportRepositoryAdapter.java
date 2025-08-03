package online.anhht.airline.planning.adapter.outbound.jpa;

import online.anhht.airline.model.Airport;
import online.anhht.airline.planning.adapter.outbound.InMemoryAirportRepository;
import online.anhht.airline.planning.port.outbound.AirportRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaAirportRepositoryAdapter implements AirportRepositoryPort {

    private final SpringDataAirportRepository springDataRepository;
    private final InMemoryAirportRepository inMemoryFallback;

    public JpaAirportRepositoryAdapter(SpringDataAirportRepository springDataRepository, InMemoryAirportRepository inMemoryFallback) {
        this.springDataRepository = springDataRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<Airport> findAll() {
        try {
            List<AirportEntity> entities = springDataRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream().map(AirportEntity::toDomain).toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Airport> findByCode(String airportCode) {
        if (airportCode == null) return Optional.empty();
        try {
            Optional<AirportEntity> entity = springDataRepository.findById(airportCode.trim().toUpperCase());
            if (entity.isPresent()) {
                return entity.map(AirportEntity::toDomain);
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByCode(airportCode);
    }

    @Override
    public void save(Airport airport) {
        inMemoryFallback.save(airport);
    }
}
