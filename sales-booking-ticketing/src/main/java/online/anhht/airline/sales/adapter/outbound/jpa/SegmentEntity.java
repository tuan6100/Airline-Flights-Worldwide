package online.anhht.airline.sales.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.TicketFlightSegment;

import java.math.BigDecimal;

@Entity
@Table(name = "segments")
@IdClass(SegmentId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SegmentEntity {

    @Id
    @Column(name = "ticket_no", length = 13)
    private String ticketNo;

    @Id
    @Column(name = "flight_id")
    private String flightId;

    @Column(name = "fare_conditions")
    private String fareConditions;

    @Column(name = "price")
    private BigDecimal price;

    public TicketFlightSegment toDomain(boolean outbound) {
        FareCondition condition = FareCondition.ECONOMY;
        if (fareConditions != null) {
            try {
                condition = FareCondition.valueOf(fareConditions.toUpperCase().trim());
            } catch (Exception ignored) {
            }
        }
        return TicketFlightSegment.of(flightId, condition, price != null ? price : BigDecimal.valueOf(100), outbound);
    }

    public static SegmentEntity fromDomain(String ticketNo, TicketFlightSegment segment) {
        return new SegmentEntity(ticketNo, segment.getFlightId(), segment.getFareCondition().name(), segment.getPrice());
    }
}
