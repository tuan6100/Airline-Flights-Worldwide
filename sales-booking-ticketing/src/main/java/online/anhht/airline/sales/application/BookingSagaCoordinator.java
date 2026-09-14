package online.anhht.airline.sales.application;

import online.anhht.airline.model.Booking;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.sales.port.inbound.BookingUseCase;
import online.anhht.airline.sales.port.outbound.FlightOperationsClientPort;
import online.anhht.airline.sales.port.outbound.InventoryServiceClientPort;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingSagaCoordinator {

    private final BookingUseCase bookingUseCase;
    private final FlightOperationsClientPort flightOperationsClient;
    private final InventoryServiceClientPort inventoryClient;

    public BookingSagaCoordinator(
            BookingUseCase bookingUseCase,
            FlightOperationsClientPort flightOperationsClient,
            InventoryServiceClientPort inventoryClient
    ) {
        this.bookingUseCase = bookingUseCase;
        this.flightOperationsClient = flightOperationsClient;
        this.inventoryClient = inventoryClient;
    }

    public record BookingSagaCommand(
            String bookRef,
            String ticketNo,
            String passengerId,
            String passengerName,
            String contactData,
            String flightId,
            String aircraftCode,
            String seatNo,
            FareCondition fareCondition,
            BigDecimal price
    ) {}

    public record BookingSagaResult(
            String bookRef,
            String ticketNo,
            BigDecimal totalAmount,
            boolean success,
            String message
    ) {}

    public BookingSagaResult executeBookingSaga(BookingSagaCommand command) {
        // Step 1: Verify flight is active and bookable
        if (!flightOperationsClient.isFlightBookable(command.flightId())) {
            return new BookingSagaResult(command.bookRef(), command.ticketNo(), BigDecimal.ZERO, false, "Flight is cancelled or not bookable");
        }

        // Step 2: Reserve seat in Inventory
        boolean reserved = inventoryClient.reserveSeat(command.flightId(), command.aircraftCode(), command.seatNo());
        if (!reserved) {
            return new BookingSagaResult(command.bookRef(), command.ticketNo(), BigDecimal.ZERO, false, "Seat " + command.seatNo() + " is unavailable or already reserved");
        }

        // Step 3: Create Booking and Ticket via BookingUseCase
        try {
            BookingUseCase.PassengerBookingRequest paxReq = getPaxReq(command);

            Booking booking = bookingUseCase.createGroupBooking(List.of(paxReq));
            Ticket ticket = booking.getTickets().isEmpty() ? null : booking.getTickets().getFirst();

            return new BookingSagaResult(
                    booking.getBookRef(),
                    ticket != null ? ticket.getTicketNo() : command.ticketNo(),
                    booking.getTotalAmount(),
                    true,
                    "Booking and Ticket issued successfully"
            );
        } catch (Exception ex) {
            // Compensating transaction: Release seat reservation
            inventoryClient.releaseSeatReservation(command.flightId(), command.aircraftCode(), command.seatNo());
            return new BookingSagaResult(command.bookRef(), command.ticketNo(), BigDecimal.ZERO, false, "Booking failed: " + ex.getMessage());
        }
    }

    private static BookingUseCase.@NonNull PassengerBookingRequest getPaxReq(BookingSagaCommand command) {
        BookingUseCase.SegmentBookingRequest segmentReq = new BookingUseCase.SegmentBookingRequest(
                command.flightId(),
                command.fareCondition(),
                command.price(),
                true
        );
        return new BookingUseCase.PassengerBookingRequest(
                command.passengerId(),
                command.passengerName(),
                command.contactData(),
                List.of(segmentReq)
        );
    }
}
