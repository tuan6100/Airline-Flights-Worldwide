package online.anhht.airline.operations.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.FlightStatus;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "flights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightEntity {

    @Id
    @Column(name = "flight_id")
    private String flightId;

    @Column(name = "route_no")
    private String routeNo;

    @Column(name = "status")
    private String status;

    @Column(name = "scheduled_departure")
    private OffsetDateTime scheduledDeparture;

    @Column(name = "scheduled_arrival")
    private OffsetDateTime scheduledArrival;

    @Column(name = "actual_departure")
    private OffsetDateTime actualDeparture;

    @Column(name = "actual_arrival")
    private OffsetDateTime actualArrival;

    public Flight toDomain() {
        Airport dep = Airport.of("SVO", "Sheremetyevo", "Moscow", "Russia", new Coordinates(55.9726, 37.4146), ZoneId.of("Europe/Moscow"), 100);
        Airport arr = Airport.of("LED", "Pulkovo", "Saint Petersburg", "Russia", new Coordinates(59.8003, 30.2625), ZoneId.of("Europe/Moscow"), 100);
        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2027, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
        );
        Route route = Route.of(routeNo != null ? routeNo : "SU001", dep, arr, Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY), LocalTime.of(8, 0), Duration.ofHours(2), validity);
        CabinLayout layout = CabinLayout.of("733", List.of(Seat.of("1A", FareCondition.BUSINESS, "733"), Seat.of("2A", FareCondition.ECONOMY, "733")));
        Airplane airplane = Airplane.of("733-01", "Boeing 737-300", 4200, 800, layout);

        OffsetDateTime depTime = scheduledDeparture != null ? scheduledDeparture : OffsetDateTime.now().plusHours(1);
        OffsetDateTime arrTime = scheduledArrival != null ? scheduledArrival : depTime.plusHours(2);

        Flight flight = Flight.of(flightId, routeNo != null ? routeNo : "SU001", route, airplane, depTime, arrTime);
        if (actualDeparture != null) flight.setActualDeparture(actualDeparture);
        if (actualArrival != null) flight.setActualArrival(actualArrival);
        if (status != null) {
            try {
                FlightStatus flightStatus = FlightStatus.valueOf(status.toUpperCase().replace(" ", "_"));
                if (flightStatus == FlightStatus.ON_TIME) {
                    flight.markOnTime();
                } else if (flightStatus == FlightStatus.BOARDING) {
                    flight.markOnTime();
                    flight.startBoarding();
                } else if (flightStatus == FlightStatus.DEPARTED) {
                    flight.markOnTime();
                    flight.startBoarding();
                    flight.depart(actualDeparture != null ? actualDeparture : OffsetDateTime.now());
                } else if (flightStatus == FlightStatus.ARRIVED) {
                    flight.markOnTime();
                    flight.startBoarding();
                    flight.depart(actualDeparture != null ? actualDeparture : OffsetDateTime.now());
                    flight.arrive(actualArrival != null ? actualArrival : OffsetDateTime.now());
                } else if (flightStatus == FlightStatus.CANCELLED) {
                    flight.cancel("Cancelled");
                }
            } catch (Exception ignored) {
            }
        }
        return flight;
    }

    public static FlightEntity fromDomain(Flight flight) {
        return new FlightEntity(
                flight.getFlightId(),
                flight.getFlightNo(),
                flight.getStatus().name(),
                flight.getScheduledDeparture(),
                flight.getScheduledArrival(),
                flight.getActualDeparture(),
                flight.getActualArrival()
        );
    }
}
