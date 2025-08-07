package online.anhht.airline.sales.adapter.inbound;

import online.anhht.airline.sales.adapter.inbound.BookingController.TicketDto;
import online.anhht.airline.sales.port.inbound.BookingUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sales/tickets")
public class TicketController {

    private final BookingUseCase bookingUseCase;

    public TicketController(BookingUseCase bookingUseCase) {
        this.bookingUseCase = bookingUseCase;
    }

    @GetMapping("/{ticketNo}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable String ticketNo) {
        return bookingUseCase.getTicketByNumber(ticketNo)
                .map(TicketDto::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
