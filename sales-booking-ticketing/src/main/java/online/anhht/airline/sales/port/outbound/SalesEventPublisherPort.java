package online.anhht.airline.sales.port.outbound;

import online.anhht.airline.events.DomainEvent;

public interface SalesEventPublisherPort {
    void publish(DomainEvent event);
}
