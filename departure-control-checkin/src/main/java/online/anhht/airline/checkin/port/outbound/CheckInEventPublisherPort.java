package online.anhht.airline.checkin.port.outbound;

import online.anhht.airline.events.DomainEvent;

public interface CheckInEventPublisherPort {
    void publish(DomainEvent event);
}
