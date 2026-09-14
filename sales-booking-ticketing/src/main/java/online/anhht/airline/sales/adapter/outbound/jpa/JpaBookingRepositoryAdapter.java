package online.anhht.airline.sales.adapter.outbound.jpa;

import online.anhht.airline.model.Booking;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.sales.adapter.outbound.InMemoryBookingRepository;
import online.anhht.airline.sales.port.outbound.BookingRepositoryPort;
import online.anhht.airline.sales.port.outbound.TicketRepositoryPort;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class JpaBookingRepositoryAdapter implements BookingRepositoryPort {

    private final SpringDataBookingRepository springDataBookingRepository;
    private final SpringDataTicketRepository springDataTicketRepository;
    private final TicketRepositoryPort ticketRepository;
    private final InMemoryBookingRepository inMemoryFallback;

    public JpaBookingRepositoryAdapter(
            SpringDataBookingRepository springDataBookingRepository,
            SpringDataTicketRepository springDataTicketRepository,
            TicketRepositoryPort ticketRepository,
            InMemoryBookingRepository inMemoryFallback
    ) {
        this.springDataBookingRepository = springDataBookingRepository;
        this.springDataTicketRepository = springDataTicketRepository;
        this.ticketRepository = ticketRepository;
        this.inMemoryFallback = inMemoryFallback;
    }

    @Override
    public List<Booking> findAll() {
        try {
            List<BookingEntity> entities = springDataBookingRepository.findAll();
            if (!entities.isEmpty()) {
                return entities.stream()
                        .map(entity -> {
                            List<TicketEntity> ticketEntities = springDataTicketRepository.findByBookRef(entity.getBookRef());
                            List<Ticket> tickets = ticketEntities.stream()
                                    .map(te -> ticketRepository.findByTicketNo(te.getTicketNo()))
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .toList();
                            return entity.toDomain(tickets);
                        })
                        .toList();
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findAll();
    }

    @Override
    public Optional<Booking> findByBookRef(String bookRef) {
        if (bookRef == null) return Optional.empty();
        try {
            Optional<BookingEntity> entityOpt = springDataBookingRepository.findById(bookRef.trim().toUpperCase());
            if (entityOpt.isPresent()) {
                BookingEntity entity = entityOpt.get();
                List<TicketEntity> ticketEntities = springDataTicketRepository.findByBookRef(entity.getBookRef());
                List<Ticket> tickets = ticketEntities.stream()
                        .map(te -> ticketRepository.findByTicketNo(te.getTicketNo()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
                return Optional.of(entity.toDomain(tickets));
            }
        } catch (Exception ignored) {
        }
        return inMemoryFallback.findByBookRef(bookRef);
    }

    @Override
    public void save(Booking booking) {
        try {
            springDataBookingRepository.save(BookingEntity.fromDomain(booking));
            ticketRepository.saveAll(booking.getTickets());
        } catch (Exception ignored) {
        }
        inMemoryFallback.save(booking);
    }
}
