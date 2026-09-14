package online.anhht.airline.sales.adapter.outbound.jpa;

import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import online.anhht.airline.sales.adapter.outbound.InMemoryTicketRepository;
import online.anhht.airline.sales.port.outbound.TicketRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaTicketRepositoryAdapter implements TicketRepositoryPort {

    private final SpringDataTicketRepository springDataTicketRepository;
    private final SpringDataSegmentRepository springDataSegmentRepository;
    private final InMemoryTicketRepository inMemoryFallback;

    public JpaTicketRepositoryAdapter(
            SpringDataTicketRepository springDataTicketRepository,
            SpringDataSegmentRepository springDataSegmentRepository,
            InMemoryTicketRepository inMemoryFallback
    ) {
        this.springDataTicketRepository = springDataTicketRepository;
        this.springDataSegmentRepository = springDataSegmentRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<Ticket> findAll() {
        try {
            List<TicketEntity> entities = springDataTicketRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream().map(this::mapTicket).toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Ticket> findByTicketNo(String ticketNo) {
        if (ticketNo == null) return Optional.empty();
        try {
            Optional<TicketEntity> entityOpt = springDataTicketRepository.findById(ticketNo.trim());
            if (entityOpt.isPresent()) {
                return Optional.of(mapTicket(entityOpt.get()));
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByTicketNo(ticketNo);
    }

    private Ticket mapTicket(TicketEntity entity) {
        List<SegmentEntity> segmentEntities = springDataSegmentRepository.findByTicketNo(entity.getTicketNo());
        List<TicketFlightSegment> segments = segmentEntities.stream()
                .map(se -> se.toDomain(entity.isOutbound()))
                .toList();
        return entity.toDomain(segments);
    }

    @Override
    public void save(Ticket ticket) {
        try {
            springDataTicketRepository.save(TicketEntity.fromDomain(ticket));
            List<SegmentEntity> segmentEntities = ticket.getSegments().stream()
                    .map(seg -> SegmentEntity.fromDomain(ticket.getTicketNo(), seg))
                    .toList();
            springDataSegmentRepository.saveAll(segmentEntities);
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(ticket);
    }

    @Override
    public void saveAll(List<Ticket> tickets) {
        for (Ticket t : tickets) {
            save(t);
        }
    }
}
