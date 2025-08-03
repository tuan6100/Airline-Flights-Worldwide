package online.anhht.airline.planning.port.outbound;

import online.anhht.airline.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepositoryPort {
    List<Route> findAll();
    Optional<Route> findByFlightNo(String flightNo);
    void save(Route route);
}
