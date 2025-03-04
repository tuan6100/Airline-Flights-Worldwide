package online.anhht.airline.checkin.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.BoardingPass;

import java.time.OffsetDateTime;

@Entity
@Table(name = "boarding_passes")
@IdClass(BoardingPassId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardingPassEntity {

    @Id
    @Column(name = "ticket_no", length = 13)
    private String ticketNo;

    @Id
    @Column(name = "flight_id")
    private String flightId;

    @Column(name = "seat_no")
    private String seatNo;

    @Column(name = "boarding_no")
    private int boardingNo;

    @Column(name = "boarding_time")
    private OffsetDateTime boardingTime;

    public BoardingPass toDomain() {
        return BoardingPass.of(
                ticketNo,
                flightId,
                boardingNo > 0 ? boardingNo : 1,
                seatNo != null ? seatNo : "1A",
                boardingTime != null ? boardingTime : OffsetDateTime.now()
        );
    }

    public static BoardingPassEntity fromDomain(BoardingPass bp) {
        return new BoardingPassEntity(
                bp.getTicketNo(),
                bp.getFlightId(),
                bp.getSeatNo(),
                bp.getBoardingNo(),
                bp.getBoardingTime()
        );
    }
}
