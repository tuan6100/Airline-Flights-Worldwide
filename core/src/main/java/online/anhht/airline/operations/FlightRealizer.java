package online.anhht.airline.operations;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Route;
import org.jspecify.annotations.NonNull;

import java.time.LocalDate;
import java.util.List;

/**
 * Realizes actual flights from route schedules exactly 60 days prior to the flight date
 * or for any specified time horizon.
 */
public interface FlightRealizer {

    /**
     * Realizes flights for a route starting from targetDate (e.g., today + 60 days).
     */
    List<Flight> realizeFlightsForHorizon(
            @NonNull Route route,
            @NonNull Airplane airplane,
            @NonNull LocalDate startDate,
            int daysAhead
    );

    /**
     * Realizes flights exactly 60 days in advance as per business domain specification.
     */
    default List<Flight> realizeFlights60DaysAdvance(@NonNull Route route, @NonNull Airplane airplane, @NonNull LocalDate today) {
        LocalDate flightDate = today.plusDays(60);
        return realizeFlightsForHorizon(route, airplane, flightDate, 0);
    }

    /**
     * Creates a new default instance of {@link FlightRealizer}.
     */
    static FlightRealizer of() {
        return new DefaultFlightRealizer();
    }
}
