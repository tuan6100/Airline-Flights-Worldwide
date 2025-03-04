package online.anhht.airline.checkin.adapter.outbound;

import online.anhht.airline.checkin.port.outbound.CheckInEventPublisherPort;
import online.anhht.airline.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class LoggingCheckInEventPublisher implements CheckInEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingCheckInEventPublisher.class);
    private final List<DomainEvent> publishedEvents = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void publish(DomainEvent event) {
        log.info("[CheckIn DomainEvent Published] Type: {}, ID: {}", event.getEventType(), event.getEventId());
        publishedEvents.add(event);
    }

    public List<DomainEvent> getPublishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
}
