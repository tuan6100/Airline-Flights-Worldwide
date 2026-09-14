package online.anhht.airline.sales.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;

import java.util.List;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketEntity {

    @Id
    @Column(name = "ticket_no", length = 13)
    private String ticketNo;

    @Column(name = "book_ref", columnDefinition = "char(6)")
    private String bookRef;

    @Column(name = "passenger_id")
    private String passengerId;

    @Column(name = "passenger_name")
    private String passengerName;

    @Column(name = "contact_data", columnDefinition = "text")
    private String contactData;

    @Column(name = "outbound")
    private boolean outbound;

    public Ticket toDomain(List<TicketFlightSegment> segments) {
        return Ticket.of(
                ticketNo,
                bookRef,
                passengerId != null ? passengerId : "PAX-001",
                passengerName != null ? passengerName : "PASSENGER",
                contactData != null ? contactData : "{}",
                segments
        );
    }

    public static TicketEntity fromDomain(Ticket ticket) {
        boolean isOutbound = ticket.getSegments().stream().anyMatch(TicketFlightSegment::isOutbound);
        return new TicketEntity(
                ticket.getTicketNo(),
                ticket.getBookRef(),
                ticket.getPassengerId(),
                ticket.getPassengerName(),
                ticket.getContactData(),
                isOutbound
        );
    }
}
