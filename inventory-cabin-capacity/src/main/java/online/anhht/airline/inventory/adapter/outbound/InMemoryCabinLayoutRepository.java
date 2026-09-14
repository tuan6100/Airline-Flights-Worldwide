package online.anhht.airline.inventory.adapter.outbound;

import online.anhht.airline.inventory.port.outbound.CabinLayoutRepositoryPort;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Seat;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCabinLayoutRepository implements CabinLayoutRepositoryPort {

    private final Map<String, CabinLayout> layouts = new ConcurrentHashMap<>();

    public InMemoryCabinLayoutRepository() {
        // Seed Boeing 777-300 layout (773)
        List<Seat> seats773 = new ArrayList<>();
        // Business Class (Rows 1-5, Seats A, B, C, D)
        for (int r = 1; r <= 5; r++) {
            for (String letter : List.of("A", "B", "C", "D")) {
                seats773.add(Seat.of(r + letter, FareCondition.BUSINESS, "773"));
            }
        }
        // Comfort Class (Rows 6-10, Seats A, B, C, D, E, F)
        for (int r = 6; r <= 10; r++) {
            for (String letter : List.of("A", "B", "C", "D", "E", "F")) {
                seats773.add(Seat.of(r + letter, FareCondition.COMFORT, "773"));
            }
        }
        // Economy Class (Rows 11-40, Seats A, B, C, D, E, F, G, H, J)
        for (int r = 11; r <= 40; r++) {
            for (String letter : List.of("A", "B", "C", "D", "E", "F", "G", "H", "J")) {
                seats773.add(Seat.of(r + letter, FareCondition.ECONOMY, "773"));
            }
        }
        CabinLayout layout773 = CabinLayout.of("773", seats773);
        save(layout773);

        // Seed Airbus A320-200 layout (320)
        List<Seat> seats320 = new ArrayList<>();
        // Business Class (Rows 1-3, Seats A, C, D, F)
        for (int r = 1; r <= 3; r++) {
            for (String letter : List.of("A", "C", "D", "F")) {
                seats320.add(Seat.of(r + letter, FareCondition.BUSINESS, "320"));
            }
        }
        // Economy Class (Rows 4-25, Seats A, B, C, D, E, F)
        for (int r = 4; r <= 25; r++) {
            for (String letter : List.of("A", "B", "C", "D", "E", "F")) {
                seats320.add(Seat.of(r + letter, FareCondition.ECONOMY, "320"));
            }
        }
        CabinLayout layout320 = CabinLayout.of("320", seats320);
        save(layout320);
    }

    @Override
    public List<CabinLayout> findAll() {
        return new ArrayList<>(layouts.values());
    }

    @Override
    public Optional<CabinLayout> findByAircraftCode(String aircraftCode) {
        if (aircraftCode == null) return Optional.empty();
        return Optional.ofNullable(layouts.get(aircraftCode.trim().toUpperCase()));
    }

    @Override
    public void save(CabinLayout cabinLayout) {
        layouts.put(cabinLayout.getAircraftCode(), cabinLayout);
    }
}
