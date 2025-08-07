package online.anhht.airline.sales.port.outbound;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface FlightOperationsClientPort {

    record FlightSummary(
            String flightId,
            String flightNo,
            String status,
            OffsetDateTime scheduledDeparture,
            OffsetDateTime scheduledArrival
    ) {}

    Optional<FlightSummary> getFlightSummary(String flightId);
    boolean isFlightBookable(String flightId);
}
