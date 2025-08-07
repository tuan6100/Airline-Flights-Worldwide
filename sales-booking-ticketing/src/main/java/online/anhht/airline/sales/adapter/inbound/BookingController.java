package online.anhht.airline.sales.adapter.inbound;

import online.anhht.airline.model.Booking;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;
import online.anhht.airline.sales.port.inbound.BookingUseCase;
import online.anhht.airline.sales.port.inbound.BookingUseCase.PassengerBookingRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sales/bookings")
public class BookingController {

    private final BookingUseCase bookingUseCase;

    public BookingController(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingUseCase.createGroupBooking(request.passengers());
        return ResponseEntity.ok(BookingResponse.fromDomain(booking));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> list = bookingUseCase.getAllBookings().stream()
                .map(BookingResponse::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{bookRef}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookRef) {
        return bookingUseCase.getBookingByRef(bookRef)
                .map(BookingResponse::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    public record CreateBookingRequest(List<PassengerBookingRequest> passengers) {}

    public record BookingResponse(
            String bookRef,
            String bookDate,
            BigDecimal totalAmount,
            int passengerCount,
            List<TicketDto> tickets
    ) {
        public static BookingResponse fromDomain(Booking b) {
            List<TicketDto> ticketDtos = b.getTickets().stream()
                    .map(TicketDto::fromDomain)
                    .toList();
            return new BookingResponse(
                    b.getBookRef(),
                    b.getBookDate().toString(),
                    b.getTotalAmount(),
                    b.getPassengerCount(),
                    ticketDtos
            );
        }
    }

    public record TicketDto(
            String ticketNo,
            String passengerId,
            String passengerName,
            BigDecimal totalPrice,
            boolean roundTrip,
            List<SegmentDto> segments
    ) {
        public static TicketDto fromDomain(Ticket t) {
            List<SegmentDto> segDtos = t.getSegments().stream()
                    .map(s -> new SegmentDto(s.getFlightId(), s.getFareCondition().name(), s.getPrice(), s.isOutbound()))
                    .toList();
            return new TicketDto(
                    t.getTicketNo(),
                    t.getPassengerId(),
                    t.getPassengerName(),
                    t.getTotalAmount(),
                    t.isRoundTrip(),
                    segDtos
            );
        }
    }

    public record SegmentDto(String flightId, String fareCondition, BigDecimal price, boolean outbound) {}
}
