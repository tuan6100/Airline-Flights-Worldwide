package online.anhht.airline.planning.adapter.outbound;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Seat;
import online.anhht.airline.planning.port.outbound.FleetRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryFleetRepository implements FleetRepositoryPort {

    private final Map<String, Airplane> fleet = new ConcurrentHashMap<>();

    public InMemoryFleetRepository() {
        // Build sample cabin layouts
        List<Seat> seats773 = new ArrayList<>();
        for (int r = 1; r <= 5; r++) {
            seats773.add(Seat.of(r + "A", FareCondition.BUSINESS, "773"));
            seats773.add(Seat.of(r + "B", FareCondition.BUSINESS, "773"));
        }
        for (int r = 6; r <= 10; r++) {
            seats773.add(Seat.of(r + "A", FareCondition.COMFORT, "773"));
            seats773.add(Seat.of(r + "B", FareCondition.COMFORT, "773"));
        }
        for (int r = 11; r <= 40; r++) {
            seats773.add(Seat.of(r + "A", FareCondition.ECONOMY, "773"));
            seats773.add(Seat.of(r + "B", FareCondition.ECONOMY, "773"));
            seats773.add(Seat.of(r + "C", FareCondition.ECONOMY, "773"));
        }

        CabinLayout layout773 = CabinLayout.of("773", seats773);
        Airplane b777 = Airplane.of("773-001", "Boeing 777-300", 11100, 905, layout773);
        Airplane b777_2 = Airplane.of("773-002", "Boeing 777-300", 11100, 905, layout773);

        List<Seat> seats320 = new ArrayList<>();
        for (int r = 1; r <= 3; r++) {
            seats320.add(Seat.of(r + "A", FareCondition.BUSINESS, "320"));
            seats320.add(Seat.of(r + "B", FareCondition.BUSINESS, "320"));
        }
        for (int r = 4; r <= 25; r++) {
            seats320.add(Seat.of(r + "A", FareCondition.ECONOMY, "320"));
            seats320.add(Seat.of(r + "B", FareCondition.ECONOMY, "320"));
        }
        CabinLayout layout320 = CabinLayout.of("320", seats320);
        Airplane a320 = Airplane.of("320-001", "Airbus A320-200", 4300, 840, layout320);
        Airplane a320_2 = Airplane.of("320-002", "Airbus A320-200", 4300, 840, layout320);

        save(b777);
        save(b777_2);
        save(a320);
        save(a320_2);
    }

    @Override
    public List<Airplane> findAll() {
        return new ArrayList<>(fleet.values());
    }

    @Override
    public Optional<Airplane> findById(String id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(fleet.get(id));
    }

    @Override
    public void save(Airplane airplane) {
        fleet.put(airplane.getId(), airplane);
    }
}
