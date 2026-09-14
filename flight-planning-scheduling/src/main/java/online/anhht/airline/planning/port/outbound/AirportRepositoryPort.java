package online.anhht.airline.planning.port.outbound;

import online.anhht.airline.model.Airport;

import java.util.List;
import java.util.Optional;

public interface AirportRepositoryPort {
    List<Airport> findAll();
    Optional<Airport> findByCode(String airportCode);
    void save(Airport airport);
}
