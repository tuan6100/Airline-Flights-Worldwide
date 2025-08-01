package online.anhht.airline.operations;

import online.anhht.airline.model.Flight;
import org.jspecify.annotations.NonNull;

/**
 * Analyzes schedule deviations between scheduled and actual/estimated flight times.
 */
public interface ScheduleDeviationAnalyzer {

    enum DelayCategory {
        ON_TIME,
        MINOR_DELAY,      // 1 to 15 minutes
        MODERATE_DELAY,   // 16 to 60 minutes
        SIGNIFICANT_DELAY,// 61 to 180 minutes
        SEVERE_DELAY      // > 180 minutes
    }

    record DeviationReport(
            String flightId,
            String flightNo,
            long departureDelayMinutes,
            long arrivalDelayMinutes,
            DelayCategory delayCategory,
            String delayReason
    ) {}

    DeviationReport analyzeDeviation(@NonNull Flight flight);

    /**
     * Creates a default {@link ScheduleDeviationAnalyzer} instance.
     */
    static ScheduleDeviationAnalyzer of() {
        return new DefaultScheduleDeviationAnalyzer();
    }
}
