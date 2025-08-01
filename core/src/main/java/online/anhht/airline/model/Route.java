package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Public class representing a planned flight Route in the airline timetable.
 */
public class Route {

    @Getter
    private final String flightNo;

    @Getter
    private final Airport departureAirport;

    @Getter
    private final Airport arrivalAirport;

    @Getter
    private final Set<DayOfWeek> daysOfWeek;

    @Getter
    private final LocalTime scheduledDepartureTime;

    @Getter
    private final Duration scheduledDuration;

    @Getter
    private final TemporalValidityRange validityRange;

    public Route(
            @NonNull String flightNo,
            @NonNull Airport departureAirport,
            @NonNull Airport arrivalAirport,
            @NonNull Set<DayOfWeek> daysOfWeek,
            @NonNull LocalTime scheduledDepartureTime,
            @NonNull Duration scheduledDuration,
            @NonNull TemporalValidityRange validityRange
    ) {
        if (departureAirport.equals(arrivalAirport)) {
            throw new IllegalArgumentException("Departure airport and arrival airport cannot be identical: " + departureAirport.getAirportCode());
        }
        if (daysOfWeek.isEmpty()) {
            throw new IllegalArgumentException("Route must operate on at least one day of the week.");
        }
        if (scheduledDuration.isNegative() || scheduledDuration.isZero()) {
            throw new IllegalArgumentException("Scheduled flight duration must be strictly positive.");
        }
        this.flightNo = flightNo.trim().toUpperCase();
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.daysOfWeek = Collections.unmodifiableSet(daysOfWeek);
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledDuration = scheduledDuration;
        this.validityRange = validityRange;
    }

    public double getDistanceKm() {
        return departureAirport.distanceTo(arrivalAirport);
    }

    public boolean isQualifiedAircraft(@NonNull Airplane airplane) {
        return airplane.isRangeQualified(getDistanceKm());
    }

    public boolean operatesOn(@NonNull DayOfWeek dayOfWeek, @NonNull OffsetDateTime dateTime) {
        return daysOfWeek.contains(dayOfWeek) && validityRange.contains(dateTime);
    }

    public static Route of(
            @NonNull String flightNo,
            @NonNull Airport departureAirport,
            @NonNull Airport arrivalAirport,
            @NonNull Set<DayOfWeek> daysOfWeek,
            @NonNull LocalTime scheduledDepartureTime,
            @NonNull Duration scheduledDuration,
            @NonNull TemporalValidityRange validityRange
    ) {
        return new Route(flightNo, departureAirport, arrivalAirport, daysOfWeek, scheduledDepartureTime, scheduledDuration, validityRange);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route route)) return false;
        return Objects.equals(flightNo, route.getFlightNo()) &&
                Objects.equals(departureAirport, route.getDepartureAirport()) &&
                Objects.equals(arrivalAirport, route.getArrivalAirport());
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightNo, departureAirport, arrivalAirport);
    }

    @Override
    public String toString() {
        return String.format("Route[%s: %s -> %s, Days: %s, DepTime: %s, Duration: %s]",
                flightNo, departureAirport.getAirportCode(), arrivalAirport.getAirportCode(),
                daysOfWeek, scheduledDepartureTime, scheduledDuration);
    }
}
