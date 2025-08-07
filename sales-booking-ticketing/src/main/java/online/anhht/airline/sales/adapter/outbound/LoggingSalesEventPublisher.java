package online.anhht.airline.sales.adapter.outbound;

import online.anhht.airline.events.DomainEvent;
import online.anhht.airline.sales.port.outbound.SalesEventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class LoggingSalesEventPublisher implements SalesEventPublisherPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingSalesEventPublisher.class);
    private final List<DomainEvent> publishedEvents = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void publish(DomainEvent event) {
        log.info("[Sales DomainEvent Published] Type: {}, ID: {}", event.getEventType(), event.getEventId());
        publishedEvents.add(event);
    }

    public List<DomainEvent> getPublishedEvents() {
        return Collections.unmodifiableList(publishedEvents);
    }
}
