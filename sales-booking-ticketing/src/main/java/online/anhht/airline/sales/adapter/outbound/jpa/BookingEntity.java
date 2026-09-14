package online.anhht.airline.sales.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.Booking;
import online.anhht.airline.model.Ticket;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingEntity {

    @Id
    @Column(name = "book_ref", columnDefinition = "char(6)")
    private String bookRef;

    @Column(name = "book_date")
    private OffsetDateTime bookDate;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    public Booking toDomain(List<Ticket> tickets) {
        Booking booking = Booking.of(bookRef, bookDate != null ? bookDate : OffsetDateTime.now());
        for (Ticket t : tickets) {
            booking.addTicket(t);
        }
        return booking;
    }

    public static BookingEntity fromDomain(Booking booking) {
        return new BookingEntity(booking.getBookRef(), booking.getBookDate(), booking.getTotalAmount());
    }
}
