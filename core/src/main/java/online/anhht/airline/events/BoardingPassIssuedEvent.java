package online.anhht.airline.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record BoardingPassIssuedEvent(
        String eventId,
        String ticketNo,
        String flightId,
        int boardingNo,
        String seatNo,
        OffsetDateTime occurredAt
) implements DomainEvent {

    public BoardingPassIssuedEvent(String ticketNo, String flightId, int boardingNo, String seatNo) {
        this(
                UUID.randomUUID().toString(),
                ticketNo,
                flightId,
                boardingNo,
                seatNo,
                OffsetDateTime.now()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "BoardingPassIssued";
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
