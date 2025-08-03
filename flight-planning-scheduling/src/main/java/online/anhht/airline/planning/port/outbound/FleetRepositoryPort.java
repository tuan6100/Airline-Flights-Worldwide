package online.anhht.airline.planning.port.outbound;

import online.anhht.airline.model.Airplane;

import java.util.List;
import java.util.Optional;

public interface FleetRepositoryPort {
    List<Airplane> findAll();
    Optional<Airplane> findById(String id);
    void save(Airplane airplane);
}
