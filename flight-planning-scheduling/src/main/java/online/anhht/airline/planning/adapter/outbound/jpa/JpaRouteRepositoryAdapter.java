package online.anhht.airline.planning.adapter.outbound.jpa;

import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.planning.adapter.outbound.InMemoryRouteRepository;
import online.anhht.airline.planning.port.outbound.AirportRepositoryPort;
import online.anhht.airline.planning.port.outbound.RouteRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaRouteRepositoryAdapter implements RouteRepositoryPort {

    private final SpringDataRouteRepository springDataRepository;
    private final InMemoryRouteRepository inMemoryFallback;
    private final AirportRepositoryPort airportRepository;

    public JpaRouteRepositoryAdapter(
            SpringDataRouteRepository springDataRepository,
            InMemoryRouteRepository inMemoryFallback,
            AirportRepositoryPort airportRepository
    ) {
        this.springDataRepository = springDataRepository;
        this.inMemoryFallback = inMemoryFallback;
        this.airportRepository = airportRepository;
    }

    @Override
    public List<Route> findAll() {
        try {
            List<RouteEntity> entities = springDataRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream()
                        .map(entity -> {
                            Airport dep = airportRepository.findByCode(entity.getDepartureAirportCode()).orElse(null);
                            Airport arr = airportRepository.findByCode(entity.getArrivalAirportCode()).orElse(null);
                            if (dep != null && arr != null) {
                                return entity.toDomain(dep, arr);
                            }
                            return null;
                        })
                        .filter(java.util.Objects::nonNull)
                        .toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Route> findByFlightNo(String flightNo) {
        if (flightNo == null) return Optional.empty();
        try {
            Optional<RouteEntity> entityOpt = springDataRepository.findById(flightNo.trim().toUpperCase());
            if (entityOpt.isPresent()) {
                RouteEntity entity = entityOpt.get();
                Airport dep = airportRepository.findByCode(entity.getDepartureAirportCode()).orElse(null);
                Airport arr = airportRepository.findByCode(entity.getArrivalAirportCode()).orElse(null);
                if (dep != null && arr != null) {
                    return Optional.of(entity.toDomain(dep, arr));
                }
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByFlightNo(flightNo);
    }

    @Override
    public void save(Route route) {
        try {
            springDataRepository.save(RouteEntity.fromDomain(route));
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(route);
    }
}
