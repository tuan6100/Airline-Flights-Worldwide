package online.anhht.airline.events;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record BookingCreatedEvent(
        String eventId,
        String bookRef,
        BigDecimal totalAmount,
        int passengerCount,
        List<String> ticketNumbers,
        OffsetDateTime occurredAt
) implements DomainEvent {

    public BookingCreatedEvent(
            String bookRef,
            BigDecimal totalAmount,
            int passengerCount,
            List<String> ticketNumbers
    ) {
        this(
                UUID.randomUUID().toString(),
                bookRef,
                totalAmount,
                passengerCount,
                List.copyOf(ticketNumbers),
                OffsetDateTime.now()
        );
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "BookingCreated";
    }

    @Override
    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }
}
