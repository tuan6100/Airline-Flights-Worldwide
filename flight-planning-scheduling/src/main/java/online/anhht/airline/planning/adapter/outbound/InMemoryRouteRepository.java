package online.anhht.airline.planning.adapter.outbound;

import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.TemporalValidityRange;
import online.anhht.airline.planning.port.outbound.AirportRepositoryPort;
import online.anhht.airline.planning.port.outbound.RouteRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRouteRepository implements RouteRepositoryPort {

    private final Map<String, Route> routes = new ConcurrentHashMap<>();

    public InMemoryRouteRepository(AirportRepositoryPort airportRepo) {
        Airport svo = airportRepo.findByCode("SVO").orElseThrow();
        Airport led = airportRepo.findByCode("LED").orElseThrow();
        Airport aer = airportRepo.findByCode("AER").orElseThrow();
        Airport jfk = airportRepo.findByCode("JFK").orElseThrow();
        Airport lhr = airportRepo.findByCode("LHR").orElseThrow();

        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route r1 = Route.of("SU001", svo, led, Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), LocalTime.of(8, 0), Duration.ofHours(1).plusMinutes(30), validity);
        Route r2 = Route.of("SU002", led, aer, Set.of(DayOfWeek.MONDAY, DayOfWeek.THURSDAY), LocalTime.of(11, 0), Duration.ofHours(3), validity);
        Route r3 = Route.of("SU100", svo, jfk, Set.of(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY), LocalTime.of(14, 0), Duration.ofHours(10), validity);
        Route r4 = Route.of("BA200", lhr, jfk, Set.of(DayOfWeek.WEDNESDAY, DayOfWeek.SUNDAY), LocalTime.of(10, 0), Duration.ofHours(8), validity);

        save(r1);
        save(r2);
        save(r3);
        save(r4);
    }

    @Override
    public List<Route> findAll() {
        return new ArrayList<>(routes.values());
    }

    @Override
    public Optional<Route> findByFlightNo(String flightNo) {
        if (flightNo == null) return Optional.empty();
        return Optional.ofNullable(routes.get(flightNo.trim().toUpperCase()));
    }

    @Override
    public void save(Route route) {
        routes.put(route.getFlightNo(), route);
    }
}
