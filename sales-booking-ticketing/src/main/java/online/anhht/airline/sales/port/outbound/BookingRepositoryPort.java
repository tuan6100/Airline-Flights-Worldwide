package online.anhht.airline.sales.port.outbound;

import online.anhht.airline.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepositoryPort {
    List<Booking> findAll();
    Optional<Booking> findByBookRef(String bookRef);
    void save(Booking booking);
}
