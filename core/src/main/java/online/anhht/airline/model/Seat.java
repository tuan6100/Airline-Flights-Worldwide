package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Public class representing a Seat on an aircraft cabin.
 */
public class Seat {

    @Getter
    private final String seatNo;

    @Getter
    private final FareCondition fareCondition;

    @Getter
    private final String aircraftCode;

    public Seat(@NonNull String seatNo, @NonNull FareCondition fareCondition, @NonNull String aircraftCode) {
        this.seatNo = seatNo.trim().toUpperCase();
        this.fareCondition = fareCondition;
        this.aircraftCode = aircraftCode;
    }

    public static Seat of(@NonNull String seatNo, @NonNull FareCondition fareCondition, @NonNull String aircraftCode) {
        return new Seat(seatNo, fareCondition, aircraftCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat seat)) return false;
        return Objects.equals(seatNo, seat.getSeatNo()) && Objects.equals(aircraftCode, seat.getAircraftCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(seatNo, aircraftCode);
    }

    @Override
    public String toString() {
        return String.format("Seat[%s (%s)]", seatNo, fareCondition);
    }
}
