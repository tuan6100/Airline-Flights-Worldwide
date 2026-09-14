package online.anhht.airline.sales.port.inbound;

import online.anhht.airline.model.Booking;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Ticket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BookingUseCase {

    record PassengerBookingRequest(
            String passengerId,
            String passengerName,
            String contactData,
            List<SegmentBookingRequest> segments
    ) {}

    record SegmentBookingRequest(
            String flightId,
            FareCondition fareCondition,
            BigDecimal price,
            boolean outbound
    ) {}

    Booking createGroupBooking(List<PassengerBookingRequest> passengerRequests);

    Optional<Booking> getBookingByRef(String bookRef);

    Optional<Ticket> getTicketByNumber(String ticketNo);

    List<Booking> getAllBookings();
}
