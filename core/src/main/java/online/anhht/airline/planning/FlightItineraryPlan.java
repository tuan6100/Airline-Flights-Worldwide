package online.anhht.airline.planning;

import online.anhht.airline.model.Route;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain value object representing a multi-leg or direct flight itinerary plan.
 */
public record FlightItineraryPlan(
        @NonNull List<Route> legs,
        double totalDistanceKm,
        @NonNull Duration totalFlightDuration,
        int layoverCount,
        @NonNull List<String> airportSequence,
        @NonNull List<String> transferAirports
) {
    public FlightItineraryPlan {
        Objects.requireNonNull(legs, "Legs list must not be null");
        Objects.requireNonNull(totalFlightDuration, "Flight duration must not be null");
        legs = List.copyOf(legs);
        airportSequence = List.copyOf(airportSequence);
        transferAirports = List.copyOf(transferAirports);
    }

    public static FlightItineraryPlan of(@NonNull List<Route> legs) {
        Objects.requireNonNull(legs, "Legs must not be null");
        if (legs.isEmpty()) {
            throw new IllegalArgumentException("Itinerary must contain at least one flight leg.");
        }

        double totalDist = 0.0;
        Duration totalDur = Duration.ZERO;
        List<String> airports = new ArrayList<>();
        List<String> transfers = new ArrayList<>();

        airports.add(legs.get(0).getDepartureAirport().getAirportCode());

        for (int i = 0; i < legs.size(); i++) {
            Route leg = legs.get(i);
            totalDist += leg.getDistanceKm();
            totalDur = totalDur.plus(leg.getScheduledDuration());
            String arrCode = leg.getArrivalAirport().getAirportCode();
            airports.add(arrCode);

            if (i < legs.size() - 1) {
                transfers.add(arrCode);
            }
        }

        int layovers = Math.max(0, legs.size() - 1);
        return new FlightItineraryPlan(legs, totalDist, totalDur, layovers, airports, transfers);
    }

    public boolean isDirect() {
        return legs.size() == 1;
    }

    public String getOriginAirportCode() {
        return airportSequence.get(0);
    }

    public String getDestinationAirportCode() {
        return airportSequence.get(airportSequence.size() - 1);
    }
}
