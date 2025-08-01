package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Public class representing a passenger Booking containing one or more group tickets.
 */
public class Booking {

    @Getter
    private final String bookRef;

    @Getter
    private final OffsetDateTime bookDate;

    @Getter
    private BigDecimal totalAmount;

    private final List<Ticket> tickets;

    public Booking(@NonNull String bookRef, @NonNull OffsetDateTime bookDate) {
        if (bookRef.length() != 6) {
            throw new IllegalArgumentException("Booking reference must be exactly 6 characters: " + bookRef);
        }
        this.bookRef = bookRef.toUpperCase();
        this.bookDate = bookDate;
        this.tickets = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    public Booking(
            @NonNull String bookRef,
            @NonNull OffsetDateTime bookDate,
            @NonNull List<Ticket> tickets
    ) {
        this(bookRef, bookDate);
        for (Ticket ticket : tickets) {
            addTicket(ticket);
        }
    }

    public synchronized void addTicket(@NonNull Ticket ticket) {
        if (!ticket.getBookRef().equalsIgnoreCase(this.bookRef)) {
            throw new IllegalArgumentException(String.format(
                    "Ticket bookRef %s does not match booking bookRef %s",
                    ticket.getBookRef(), this.bookRef
            ));
        }
        this.tickets.add(ticket);
        this.totalAmount = this.totalAmount.add(ticket.getTotalAmount());
    }

    public synchronized List<Ticket> getTickets() {
        return Collections.unmodifiableList(tickets);
    }

    public synchronized int getPassengerCount() {
        return tickets.size();
    }

    public static Booking of(@NonNull String bookRef, @NonNull OffsetDateTime bookDate) {
        return new Booking(bookRef, bookDate);
    }

    public static Booking of(@NonNull String bookRef, @NonNull OffsetDateTime bookDate, @NonNull List<Ticket> tickets) {
        return new Booking(bookRef, bookDate, tickets);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking booking)) return false;
        return Objects.equals(bookRef, booking.getBookRef());
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookRef);
    }

    @Override
    public String toString() {
        return String.format("Booking[%s, Date: %s, Passengers: %d, Total: %s]",
                bookRef, bookDate, tickets.size(), totalAmount);
    }
}
