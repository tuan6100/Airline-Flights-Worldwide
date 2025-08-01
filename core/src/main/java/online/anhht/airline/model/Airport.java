package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.SequencedMap;

/**
 * Public class representing an Airport node in the global flight network.
 */
public class Airport {

    @Getter
    private final String airportCode;

    @Getter
    private final String airportName;

    @Getter
    private final String city;

    @Getter
    private final String country;

    @Getter
    private final Coordinates coordinates;

    @Getter
    private final ZoneId timezone;

    @Getter
    private int capacity;

    private final SequencedMap<String, Airplane> parkedAirplanes;

    public Airport(
            @NonNull String airportCode,
            @NonNull String airportName,
            @NonNull String city,
            @NonNull String country,
            @NonNull Coordinates coordinates,
            @NonNull ZoneId timezone,
            int capacity
    ) {
        if (airportCode.length() != 3) {
            throw new IllegalArgumentException("Airport IATA code must be exactly 3 characters: " + airportCode);
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Airport capacity must be positive: " + capacity);
        }
        this.airportCode = airportCode.toUpperCase();
        this.airportName = airportName;
        this.city = city;
        this.country = country;
        this.coordinates = coordinates;
        this.timezone = timezone;
        this.capacity = capacity;
        this.parkedAirplanes = LinkedHashMap.newLinkedHashMap(capacity);
    }

    public synchronized void addAirplane(@NonNull Airplane airplane) {
        if (parkedAirplanes.size() >= capacity) {
            throw new IllegalStateException(String.format("Airport %s is packed to capacity (%d).", airportCode, capacity));
        }
        parkedAirplanes.putLast(airplane.getId(), airplane);
    }

    public synchronized void removeAirplane(@NonNull Airplane airplane) {
        parkedAirplanes.remove(airplane.getId());
    }

    public synchronized int getParkedAirplaneCount() {
        return parkedAirplanes.size();
    }

    public synchronized boolean hasCapacity() {
        return parkedAirplanes.size() < capacity;
    }

    public synchronized boolean isAirplaneParked(String airplaneId) {
        return parkedAirplanes.containsKey(airplaneId);
    }

    public synchronized void setCapacity(int newCapacity) {
        if (newCapacity <= 0) {
            throw new IllegalArgumentException("Airport capacity must be positive");
        }
        if (newCapacity < parkedAirplanes.size()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot decrease capacity to %d for airport %s: %d aircraft currently parked.",
                    newCapacity, airportCode, parkedAirplanes.size()
            ));
        }
        this.capacity = newCapacity;
    }

    public double distanceTo(@NonNull Airport other) {
        return this.coordinates.distanceTo(other.getCoordinates());
    }

    public static Airport of(
            @NonNull String airportCode,
            @NonNull String airportName,
            @NonNull String city,
            @NonNull String country,
            @NonNull Coordinates coordinates,
            @NonNull ZoneId timezone,
            int capacity
    ) {
        return new Airport(airportCode, airportName, city, country, coordinates, timezone, capacity);
    }

    public static Airport of(String airportCode, int capacity) {
        return new Airport(airportCode, airportCode, "Unknown City", "Unknown Country", new Coordinates(0.0, 0.0), ZoneId.of("UTC"), capacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Airport other)) return false;
        return Objects.equals(airportCode, other.getAirportCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(airportCode);
    }

    @Override
    public String toString() {
        return String.format("Airport[%s - %s (%s, %s)]", airportCode, airportName, city, country);
    }
}
