package online.anhht.airline.operations;

import online.anhht.airline.model.Flight;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Default implementation of {@link ScheduleDeviationAnalyzer}.
 */
public class DefaultScheduleDeviationAnalyzer implements ScheduleDeviationAnalyzer {

    @Override
    public DeviationReport analyzeDeviation(@NonNull Flight flight) {
        Objects.requireNonNull(flight, "Flight must not be null");

        long depDelay = flight.getDepartureDelayMinutes();
        long arrDelay = flight.getArrivalDelayMinutes();
        long maxDelay = Math.max(depDelay, arrDelay);

        DelayCategory category;
        if (maxDelay <= 0) {
            category = DelayCategory.ON_TIME;
        } else if (maxDelay <= 15) {
            category = DelayCategory.MINOR_DELAY;
        } else if (maxDelay <= 60) {
            category = DelayCategory.MODERATE_DELAY;
        } else if (maxDelay <= 180) {
            category = DelayCategory.SIGNIFICANT_DELAY;
        } else {
            category = DelayCategory.SEVERE_DELAY;
        }

        return new DeviationReport(
                flight.getFlightId(),
                flight.getFlightNo(),
                depDelay,
                arrDelay,
                category,
                flight.getDelayReason() != null ? flight.getDelayReason() : "No delay recorded"
        );
    }
}
