package online.anhht.airline.planning.adapter.outbound.jpa;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.planning.adapter.outbound.InMemoryFleetRepository;
import online.anhht.airline.planning.port.outbound.FleetRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaFleetRepositoryAdapter implements FleetRepositoryPort {

    private final SpringDataAirplaneRepository springDataRepository;
    private final InMemoryFleetRepository inMemoryFallback;

    public JpaFleetRepositoryAdapter(SpringDataAirplaneRepository springDataRepository, InMemoryFleetRepository inMemoryFallback) {
        this.springDataRepository = springDataRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<Airplane> findAll() {
        try {
            List<AirplaneEntity> entities = springDataRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream().map(AirplaneEntity::toDomain).toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Airplane> findById(String id) {
        if (id == null) return Optional.empty();
        try {
            Optional<AirplaneEntity> entity = springDataRepository.findById(id.trim().toUpperCase());
            if (entity.isPresent()) {
                return entity.map(AirplaneEntity::toDomain);
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findById(id);
    }

    @Override
    public void save(Airplane airplane) {
        try {
            springDataRepository.save(AirplaneEntity.fromDomain(airplane));
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(airplane);
    }
}
