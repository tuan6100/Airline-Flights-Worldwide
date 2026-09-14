package online.anhht.airline.planning.adapter.outbound;

import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.planning.port.outbound.AirportRepositoryPort;
import org.springframework.stereotype.Repository;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryAirportRepository implements AirportRepositoryPort {

    private final Map<String, Airport> airports = new ConcurrentHashMap<>();

    public InMemoryAirportRepository() {
        // Seed standard airports
        Airport svo = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.9726, 37.4146), ZoneId.of("Europe/Moscow"), 120);
        Airport led = Airport.of("LED", "Pulkovo", "Saint Petersburg", "Russia", new Coordinates(59.8003, 30.2625), ZoneId.of("Europe/Moscow"), 80);
        Airport aer = Airport.of("AER", "Sochi International", "Sochi", "Russia", new Coordinates(43.4499, 39.9566), ZoneId.of("Europe/Moscow"), 60);
        Airport jfk = Airport.of("JFK", "John F. Kennedy", "New York", "USA", new Coordinates(40.6413, -73.7781), ZoneId.of("America/New_York"), 150);
        Airport lhr = Airport.of("LHR", "Heathrow", "London", "UK", new Coordinates(51.4700, -0.4543), ZoneId.of("Europe/London"), 140);

        save(svo);
        save(led);
        save(aer);
        save(jfk);
        save(lhr);
    }

    @Override
    public List<Airport> findAll() {
        return new ArrayList<>(airports.values());
    }

    @Override
    public Optional<Airport> findByCode(String airportCode) {
        if (airportCode == null) return Optional.empty();
        return Optional.ofNullable(airports.get(airportCode.trim().toUpperCase()));
    }

    @Override
    public void save(Airport airport) {
        airports.put(airport.getAirportCode(), airport);
    }
}
