package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Public class representing an aircraft Cabin Layout and seat configurations.
 */
public class CabinLayout {

    @Getter
    private final String aircraftCode;

    @Getter
    private final List<Seat> seats;

    private final Map<FareCondition, Integer> capacityByClass;

    public CabinLayout(@NonNull String aircraftCode, @NonNull List<Seat> seats) {
        if (seats.isEmpty()) {
            throw new IllegalArgumentException("Cabin layout must contain at least one seat.");
        }
        this.aircraftCode = aircraftCode;
        this.seats = List.copyOf(seats);

        Map<FareCondition, Integer> map = new EnumMap<>(FareCondition.class);
        for (FareCondition fc : FareCondition.values()) {
            map.put(fc, 0);
        }
        for (Seat seat : this.seats) {
            map.put(seat.getFareCondition(), map.get(seat.getFareCondition()) + 1);
        }
        this.capacityByClass = Collections.unmodifiableMap(map);
    }

    public int getTotalCapacity() {
        return seats.size();
    }

    public int getCapacity(FareCondition fareCondition) {
        return capacityByClass.getOrDefault(fareCondition, 0);
    }

    public Optional<Seat> findSeat(String seatNo) {
        if (seatNo == null) return Optional.empty();
        String normalized = seatNo.trim().toUpperCase();
        return seats.stream().filter(s -> s.getSeatNo().equalsIgnoreCase(normalized)).findFirst();
    }

    public boolean hasSeat(String seatNo) {
        return findSeat(seatNo).isPresent();
    }

    public static CabinLayout of(@NonNull String aircraftCode, @NonNull List<Seat> seats) {
        return new CabinLayout(aircraftCode, seats);
    }
}
