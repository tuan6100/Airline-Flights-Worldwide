package online.anhht.airline.events;

import online.anhht.airline.model.FlightStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public record FlightScheduledEvent(
        String eventId,
        String flightId,
        String flightNo,
        String departureAirportCode,
        String arrivalAirportCode,
        OffsetDateTime scheduledDeparture,
        OffsetDateTime scheduledArrival,
        OffsetDateTime occurredAt
) implements DomainEvent {

    public FlightScheduledEvent(
            String flightId,
            String flightNo,
            String departureAirportCode,
            String arrivalAirportCode,
            OffsetDateTime scheduledDeparture,
            OffsetDateTime scheduledArrival
    ) {
        this(
                UUID.randomUUID().toString(),
                flightId,
                flightNo,
                departureAirportCode,
                arrivalAirportCode,
                scheduledDeparture,
                scheduledArrival,
                OffsetDateTime.now()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "FlightScheduled";
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
