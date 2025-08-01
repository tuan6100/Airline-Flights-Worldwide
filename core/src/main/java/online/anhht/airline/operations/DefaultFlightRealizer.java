package online.anhht.airline.operations;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Route;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default implementation of {@link FlightRealizer}.
 */
public class DefaultFlightRealizer implements FlightRealizer {

    private final AtomicLong flightIdSequence;

    public DefaultFlightRealizer() {
        this(100000L);
    }

    public DefaultFlightRealizer(long initialSequence) {
        this.flightIdSequence = new AtomicLong(initialSequence);
    }

    @Override
    public List<Flight> realizeFlightsForHorizon(
            @NonNull Route route,
            @NonNull Airplane airplane,
            @NonNull LocalDate startDate,
            int daysAhead
    ) {
        Objects.requireNonNull(route, "Route must not be null");
        Objects.requireNonNull(airplane, "Airplane must not be null");
        if (!route.isQualifiedAircraft(airplane)) {
            throw new IllegalArgumentException(String.format(
                    "Aircraft %s (Range %d km) is not qualified for route %s (Distance %.2f km)",
                    airplane.getId(), airplane.getRangeKm(), route.getFlightNo(), route.getDistanceKm()
            ));
        }

        List<Flight> realizedFlights = new ArrayList<>();
        LocalDate curDate = startDate;
        LocalDate endDate = startDate.plusDays(daysAhead);

        while (!curDate.isAfter(endDate)) {
            OffsetDateTime depTime = curDate.atTime(route.getScheduledDepartureTime()).atOffset(ZoneOffset.UTC);
            if (route.operatesOn(curDate.getDayOfWeek(), depTime)) {
                OffsetDateTime arrTime = depTime.plus(route.getScheduledDuration());
                String flightId = String.valueOf(flightIdSequence.incrementAndGet());
                Flight flight = Flight.of(flightId, route.getFlightNo(), route, airplane, depTime, arrTime);
                realizedFlights.add(flight);
            }
            curDate = curDate.plusDays(1);
        }

        return realizedFlights;
    }
}
