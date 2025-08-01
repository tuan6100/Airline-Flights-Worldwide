package online.anhht.airline.events;

import online.anhht.airline.model.FlightStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FlightStatusChangedEvent(
        String eventId,
        String flightId,
        String flightNo,
        FlightStatus previousStatus,
        FlightStatus newStatus,
        String reason,
        OffsetDateTime occurredAt
) implements DomainEvent {

    public FlightStatusChangedEvent(
            String flightId,
            String flightNo,
            FlightStatus previousStatus,
            FlightStatus newStatus,
            String reason
    ) {
        this(
                UUID.randomUUID().toString(),
                flightId,
                flightNo,
                previousStatus,
                newStatus,
                reason,
                OffsetDateTime.now()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "FlightStatusChanged";
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
