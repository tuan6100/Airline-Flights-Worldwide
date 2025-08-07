package online.anhht.airline.sales.adapter.outbound.rest;

import online.anhht.airline.sales.port.outbound.FlightOperationsClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class RestFlightOperationsClientAdapter implements FlightOperationsClientPort {

    private final RestClient restClient;

    public RestFlightOperationsClientAdapter(
            @Value("${services.flight-operations.url:http://localhost:8001}") String baseUrl
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<FlightSummary> getFlightSummary(String flightId) {
        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("/api/v1/operations/flights/{flightId}", flightId)
                            .retrieve()
                            .body(FlightSummary.class)
            );
        } catch (Exception ex) {
            return Optional.of(new FlightSummary(flightId, "SU001", "SCHEDULED", OffsetDateTime.now().plusHours(2), OffsetDateTime.now().plusHours(4)));
        }
    }

    @Override
    public boolean isFlightBookable(String flightId) {
        return getFlightSummary(flightId)
                .map(f -> !"CANCELLED".equalsIgnoreCase(f.status()) && !"DEPARTED".equalsIgnoreCase(f.status()))
                .orElse(true);
    }
}
