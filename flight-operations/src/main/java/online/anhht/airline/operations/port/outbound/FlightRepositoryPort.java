package online.anhht.airline.operations.port.outbound;

import online.anhht.airline.model.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightRepositoryPort {
    List<Flight> findAll();
    Optional<Flight> findById(String flightId);
    void save(Flight flight);
    void saveAll(List<Flight> flights);
}
