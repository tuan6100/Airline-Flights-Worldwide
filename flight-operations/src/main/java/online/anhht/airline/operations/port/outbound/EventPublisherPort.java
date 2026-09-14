package online.anhht.airline.operations.port.outbound;

import online.anhht.airline.events.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
