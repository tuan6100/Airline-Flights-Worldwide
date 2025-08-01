package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Public class representing an Airplane entity managing flight/parking state transitions.
 */
public class Airplane {

    @Getter
    private final String id;

    @Getter
    private final String model;

    @Getter
    private final int rangeKm;

    @Getter
    private final int cruisingSpeedKmH;

    @Getter
    private final CabinLayout cabinLayout;

    @Getter
    private AirplaneState state;

    @Getter
    private Airport currentAirport;

    public Airplane(
            @NonNull String id,
            @NonNull String model,
            int rangeKm,
            int cruisingSpeedKmH,
            @NonNull CabinLayout cabinLayout
    ) {
        if (rangeKm <= 0) {
            throw new IllegalArgumentException("Range must be positive: " + rangeKm);
        }
        if (cruisingSpeedKmH <= 0) {
            throw new IllegalArgumentException("Cruising speed must be positive: " + cruisingSpeedKmH);
        }
        this.id = id;
        this.model = model;
        this.rangeKm = rangeKm;
        this.cruisingSpeedKmH = cruisingSpeedKmH;
        this.cabinLayout = cabinLayout;
        this.state = new AirplaneParkingState();
    }

    public void changeState(@NonNull AirplaneState newState) {
        this.state = newState;
    }

    public boolean isRangeQualified(double distanceKm) {
        return this.rangeKm >= distanceKm;
    }

    public synchronized void parkAt(@NonNull Airport airport) {
        airport.addAirplane(this);
        this.currentAirport = airport;
        this.state = new AirplaneParkingState();
    }

    public synchronized void takeOff(@NonNull Airport departureAirport) {
        state.handleTakeOff(this);
        departureAirport.removeAirplane(this);
        this.currentAirport = null;
    }

    public synchronized void land(@NonNull Airport arrivalAirport) {
        state.handleLanding(this);
        arrivalAirport.addAirplane(this);
        this.currentAirport = arrivalAirport;
    }

    public synchronized void enterMaintenance() {
        state.handleEnterMaintenance(this);
    }

    public synchronized void exitMaintenance() {
        state.handleExitMaintenance(this);
    }

    public int getSeatingCapacity() {
        return cabinLayout.getTotalCapacity();
    }

    public static Airplane of(
            @NonNull String id,
            @NonNull String model,
            int rangeKm,
            int cruisingSpeedKmH,
            @NonNull CabinLayout cabinLayout
    ) {
        return new Airplane(id, model, rangeKm, cruisingSpeedKmH, cabinLayout);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airplane airplane)) return false;
        return Objects.equals(id, airplane.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Airplane[%s - %s (Range: %d km, Speed: %d km/h, State: %s)]",
                id, model, rangeKm, cruisingSpeedKmH, state.getStateName());
    }
}
