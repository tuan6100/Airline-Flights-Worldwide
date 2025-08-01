package online.anhht.airline.events;

import java.time.OffsetDateTime;

/**
 * Base contract for domain events across microservices.
 */
public interface DomainEvent {
    String getEventId();
    String getEventType();
    OffsetDateTime getOccurredAt();
}
