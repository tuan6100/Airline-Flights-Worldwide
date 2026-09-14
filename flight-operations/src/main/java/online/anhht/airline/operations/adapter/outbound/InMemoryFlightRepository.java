package online.anhht.airline.operations.adapter.outbound;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;
import online.anhht.airline.operations.port.outbound.FlightRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFlightRepository implements FlightRepositoryPort {

    private final Map<String, Flight> flights = new ConcurrentHashMap<>();

    public InMemoryFlightRepository() {
        // Seed initial flight for demonstration and testing
        Airport svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.9726, 37.4146), ZoneId.of("Europe/Moscow"), 100);
        Airport led = Airport.of("LED", "Pulkovo", "Saint Petersburg", "Russia", new Coordinates(59.8003, 30.2625), ZoneId.of("Europe/Moscow"), 100);

        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route route = Route.of("SU001", svo, led, Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), LocalTime.of(8, 0), Duration.ofHours(1).plusMinutes(30), validity);

        Seat s1 = Seat.of("1A", FareCondition.BUSINESS, "320");
        Seat s2 = Seat.of("2A", FareCondition.ECONOMY, "320");
        CabinLayout layout = CabinLayout.of("320", List.of(s1, s2));
        Airplane airplane = Airplane.of("320-001", "Airbus A320-200", 4300, 840, layout);

        OffsetDateTime dep = OffsetDateTime.now().plusHours(2);
        OffsetDateTime arr = dep.plusHours(1).plusMinutes(30);

        Flight flight = Flight.of("FL-1001", "SU001", route, airplane, dep, arr);
        save(flight);
    }

    @Override
    public List<Flight> findAll() {
        return new ArrayList<>(flights.values());
    }

    @Override
    public Optional<Flight> findById(String flightId) {
        if (flightId == null) return Optional.empty();
        return Optional.ofNullable(flights.get(flightId));
    }

    @Override
    public void save(Flight flight) {
        flights.put(flight.getFlightId(), flight);
    }

    @Override
    public void saveAll(List<Flight> flightList) {
        for (Flight f : flightList) {
            save(f);
        }
    }
}
