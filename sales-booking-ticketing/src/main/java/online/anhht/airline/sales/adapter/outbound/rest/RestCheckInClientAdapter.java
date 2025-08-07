package online.anhht.airline.sales.adapter.outbound.rest;

import online.anhht.airline.sales.port.outbound.CheckInServiceClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
public class RestCheckInClientAdapter implements CheckInServiceClientPort {

    private final RestClient restClient;

    public RestCheckInClientAdapter(
            @Value("${services.checkin.url:http://localhost:8004}") String baseUrl
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<BoardingPassSummary> requestBoardingPass(String ticketNo, String flightId, String seatNo) {
        try {
            return Optional.ofNullable(
                    restClient.post()
                            .uri("/api/v1/checkin/process")
                            .body(new CheckInRequestDto(ticketNo, flightId, seatNo))
                            .retrieve()
                            .body(BoardingPassSummary.class)
            );
        } catch (Exception ex) {
            return Optional.of(new BoardingPassSummary(ticketNo, flightId, 1, seatNo != null ? seatNo : "1A", OffsetDateTime.now()));
        }
    }

    private record CheckInRequestDto(String ticketNo, String flightId, String seatNo) {}
}
