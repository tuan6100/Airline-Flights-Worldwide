package online.anhht.airline.model;

import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Public class representing an operational Flight instance with State pattern transitions.
 */
public class Flight {

    @Getter
    private final String flightId;

    @Getter
    private final String flightNo;

    @Getter
    private final Route route;

    @Getter
    private final Airplane airplane;

    @Getter
    private final OffsetDateTime scheduledDeparture;

    @Getter
    private final OffsetDateTime scheduledArrival;

    @Getter
    private FlightState state;

    @Getter @Setter
    private OffsetDateTime estimatedDeparture;

    @Getter @Setter
    private OffsetDateTime actualDeparture;

    @Getter @Setter
    private OffsetDateTime actualArrival;

    @Getter @Setter
    private String delayReason;

    @Getter @Setter
    private String cancelReason;

    public Flight(
            @NonNull String flightId,
            @NonNull String flightNo,
            @NonNull Route route,
            @NonNull Airplane airplane,
            @NonNull OffsetDateTime scheduledDeparture,
            @NonNull OffsetDateTime scheduledArrival
    ) {
        if (!scheduledArrival.isAfter(scheduledDeparture)) {
            throw new IllegalArgumentException(String.format(
                    "Scheduled arrival (%s) must be after scheduled departure (%s)",
                    scheduledArrival, scheduledDeparture
            ));
        }
        this.flightId = flightId;
        this.flightNo = flightNo.trim().toUpperCase();
        this.route = route;
        this.airplane = airplane;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledArrival = scheduledArrival;
        this.state = new ScheduledFlightState();
    }

    public void changeState(@NonNull FlightState newState) {
        this.state = newState;
    }

    public FlightStatus getStatus() {
        return state.getStatus();
    }

    public void markOnTime() {
        state.markOnTime(this);
    }

    public void markDelayed(@NonNull OffsetDateTime expectedDeparture, @NonNull String reason) {
        state.markDelayed(this, expectedDeparture, reason);
    }

    public void startBoarding() {
        state.startBoarding(this);
    }

    public void depart(@NonNull OffsetDateTime actualDeparture) {
        state.depart(this, actualDeparture);
        airplane.takeOff(route.getDepartureAirport());
    }

    public void arrive(@NonNull OffsetDateTime actualArrival) {
        state.arrive(this, actualArrival);
        airplane.land(route.getArrivalAirport());
    }

    public void cancel(@NonNull String reason) {
        state.cancel(this, reason);
    }

    public long getDepartureDelayMinutes() {
        OffsetDateTime effectiveDep = actualDeparture != null ? actualDeparture : estimatedDeparture;
        if (effectiveDep == null) {
            return 0L;
        }
        return Duration.between(scheduledDeparture, effectiveDep).toMinutes();
    }

    public long getArrivalDelayMinutes() {
        if (actualArrival == null) {
            return 0L;
        }
        return Duration.between(scheduledArrival, actualArrival).toMinutes();
    }

    public boolean isDelayed() {
        return getStatus() == FlightStatus.DELAYED || getDepartureDelayMinutes() > 15;
    }

    public ZonedDateTime getScheduledDepartureUtc() {
        return scheduledDeparture.atZoneSameInstant(ZoneOffset.UTC);
    }

    public ZonedDateTime getScheduledArrivalUtc() {
        return scheduledArrival.atZoneSameInstant(ZoneOffset.UTC);
    }

    public ZonedDateTime getScheduledDepartureLocal() {
        ZoneId departureTz = route.getDepartureAirport().getTimezone();
        return scheduledDeparture.atZoneSameInstant(departureTz);
    }

    public ZonedDateTime getScheduledArrivalLocal() {
        ZoneId arrivalTz = route.getArrivalAirport().getTimezone();
        return scheduledArrival.atZoneSameInstant(arrivalTz);
    }

    public static Flight of(
            @NonNull String flightId,
            @NonNull String flightNo,
            @NonNull Route route,
            @NonNull Airplane airplane,
            @NonNull OffsetDateTime scheduledDeparture,
            @NonNull OffsetDateTime scheduledArrival
    ) {
        return new Flight(flightId, flightNo, route, airplane, scheduledDeparture, scheduledArrival);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Flight flight)) return false;
        return Objects.equals(flightId, flight.getFlightId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(flightId);
    }

    @Override
    public String toString() {
        return String.format("Flight[%s (%s): %s -> %s, Status: %s, Dep: %s, Arr: %s]",
                flightId, flightNo, route.getDepartureAirport().getAirportCode(),
                route.getArrivalAirport().getAirportCode(), getStatus(), scheduledDeparture, scheduledArrival);
    }
}
