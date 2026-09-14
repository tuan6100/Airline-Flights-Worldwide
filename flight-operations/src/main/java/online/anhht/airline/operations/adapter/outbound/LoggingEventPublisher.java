package online.anhht.airline.operations.adapter.outbound;

import online.anhht.airline.events.DomainEvent;
import online.anhht.airline.operations.port.outbound.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class LoggingEventPublisher implements EventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingEventPublisher.class);
    private final List<DomainEvent> publishedEvents = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void publish(DomainEvent event) {
        log.info("[DomainEvent Published] Type: {}, ID: {}, OccurredAt: {}",
                event.getEventType(), event.getEventId(), event.getOccurredAt());
        publishedEvents.add(event);
    }

    public List<DomainEvent> getPublishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
}
