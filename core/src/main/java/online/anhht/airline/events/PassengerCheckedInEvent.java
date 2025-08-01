package online.anhht.airline.events;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PassengerCheckedInEvent(
        String eventId,
        String ticketNo,
        String passengerName,
        List<String> flightIds,
        OffsetDateTime occurredAt
) implements DomainEvent {

    public PassengerCheckedInEvent(String ticketNo, String passengerName, List<String> flightIds) {
        this(
                UUID.randomUUID().toString(),
                ticketNo,
                passengerName,
                List.copyOf(flightIds),
                OffsetDateTime.now()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "PassengerCheckedIn";
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
