package online.anhht.airline.sales.application;

import online.anhht.airline.events.BookingCreatedEvent;
import online.anhht.airline.model.Booking;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import online.anhht.airline.sales.port.inbound.BookingUseCase;
import online.anhht.airline.sales.port.outbound.BookingRepositoryPort;
import online.anhht.airline.sales.port.outbound.SalesEventPublisherPort;
import online.anhht.airline.sales.port.outbound.TicketRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BookingApplicationService implements BookingUseCase {

    private final BookingRepositoryPort bookingRepo;
    private final TicketRepositoryPort ticketRepo;
    private final SalesEventPublisherPort eventPublisher;
    private final AtomicLong ticketNoSequence = new AtomicLong(5432000000000L);
    private final Random random = new Random();

    public BookingApplicationService(
            BookingRepositoryPort bookingRepo,
            TicketRepositoryPort ticketRepo,
            SalesEventPublisherPort eventPublisher
    ) {
        this.bookingRepo = bookingRepo;
        this.ticketRepo = ticketRepo;
        this.eventPublisher = eventPublisher;
    }

    private String generateBookRef() {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateTicketNumber() {
        return String.format("%013d", ticketNoSequence.incrementAndGet());
    }

    @Override
    public Booking createGroupBooking(List<PassengerBookingRequest> passengerRequests) {
        if (passengerRequests.isEmpty()) {
            throw new IllegalArgumentException("Booking must contain at least one passenger.");
        }

        String bookRef = generateBookRef();
        OffsetDateTime bookDate = OffsetDateTime.now(ZoneId.of("UTC"));
        Booking booking = Booking.of(bookRef, bookDate);

        List<Ticket> tickets = new ArrayList<>();
        List<String> ticketNumbers = new ArrayList<>();

        for (PassengerBookingRequest paxReq : passengerRequests) {
            List<TicketFlightSegment> segments = paxReq.segments().stream()
                    .map(s -> TicketFlightSegment.of(s.flightId(), s.fareCondition(), s.price(), s.outbound()))
                    .toList();

            String ticketNo = generateTicketNumber();
            Ticket ticket = Ticket.of(
                    ticketNo,
                    bookRef,
                    paxReq.passengerId(),
                    paxReq.passengerName(),
                    paxReq.contactData(),
                    segments
            );
            booking.addTicket(ticket);
            tickets.add(ticket);
            ticketNumbers.add(ticketNo);
        }

        bookingRepo.save(booking);
        ticketRepo.saveAll(tickets);

        eventPublisher.publish(new BookingCreatedEvent(
                bookRef, booking.getTotalAmount(), booking.getPassengerCount(), ticketNumbers
        ));

        return booking;
    }

    @Override
    public Optional<Booking> getBookingByRef(String bookRef) {
        return bookingRepo.findByBookRef(bookRef);
    }

    @Override
    public Optional<Ticket> getTicketByNumber(String ticketNo) {
        return ticketRepo.findByTicketNo(ticketNo);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }
}
