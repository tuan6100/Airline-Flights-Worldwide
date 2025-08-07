package online.anhht.airline.sales.port.outbound;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface CheckInServiceClientPort {

    record BoardingPassSummary(
            String ticketNo,
            String flightId,
            int boardingNo,
            String seatNo,
            OffsetDateTime boardingTime
    ) {}

    Optional<BoardingPassSummary> requestBoardingPass(String ticketNo, String flightId, String seatNo);
}
