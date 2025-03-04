package online.anhht.airline.inventory.port.outbound;

import online.anhht.airline.model.CabinLayout;

import java.util.List;
import java.util.Optional;

public interface CabinLayoutRepositoryPort {
    List<CabinLayout> findAll();
    Optional<CabinLayout> findByAircraftCode(String aircraftCode);
    void save(CabinLayout cabinLayout);
}
