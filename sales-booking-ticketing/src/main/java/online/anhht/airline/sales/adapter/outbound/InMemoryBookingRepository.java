package online.anhht.airline.sales.adapter.outbound;

import online.anhht.airline.model.Booking;
import online.anhht.airline.sales.port.outbound.BookingRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepositoryPort {

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public Optional<Booking> findByBookRef(String bookRef) {
        if (bookRef == null) return Optional.empty();
        return Optional.ofNullable(bookings.get(bookRef.trim().toUpperCase()));
    }

    @Override
    public void save(Booking booking) {
        bookings.put(booking.getBookRef(), booking);
    }
}
