package online.anhht.airline.planning.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.TemporalValidityRange;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteEntity {

    @Id
    @Column(name = "route_no")
    private String routeNo;

    @Column(name = "departure_airport", columnDefinition = "char(3)")
    private String departureAirportCode;

    @Column(name = "arrival_airport", columnDefinition = "char(3)")
    private String arrivalAirportCode;

    @Column(name = "airplane_code", columnDefinition = "char(3)")
    private String airplaneCode;

    @Column(name = "days_of_week")
    private String daysOfWeek;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    public Route toDomain(Airport dep, Airport arr) {
        Set<DayOfWeek> days = new HashSet<>();
        if (daysOfWeek != null && !daysOfWeek.isBlank()) {
            for (String part : daysOfWeek.split(",")) {
                try {
                    days.add(DayOfWeek.of(Integer.parseInt(part.trim())));
                } catch (Exception ignored) {
                }
            }
        }
        if (days.isEmpty()) {
            days = Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        }
        LocalTime depTime = scheduledTime != null ? scheduledTime : LocalTime.of(8, 0);
        Duration dur = durationMinutes != null ? Duration.ofMinutes(durationMinutes) : Duration.ofHours(2);
        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2027, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        );
        return Route.of(routeNo, dep, arr, days, depTime, dur, validity);
    }

    public static RouteEntity fromDomain(Route route) {
        String days = route.getDaysOfWeek().stream()
                .map(d -> String.valueOf(d.getValue()))
                .collect(Collectors.joining(","));
        return new RouteEntity(
                route.getFlightNo(),
                route.getDepartureAirport().getAirportCode(),
                route.getArrivalAirport().getAirportCode(),
                "733",
                days,
                route.getScheduledDepartureTime(),
                (int) route.getScheduledDuration().toMinutes()
        );
    }
}
