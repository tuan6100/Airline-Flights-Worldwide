package online.anhht.airline.model;

import lombok.Getter;
import org.jspecify.annotations.NonNull;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Public class representing an issued Boarding Pass for a checked-in flight passenger.
 */
public class BoardingPass {

    @Getter
    private final String ticketNo;

    @Getter
    private final String flightId;

    @Getter
    private final int boardingNo;

    @Getter
    private final String seatNo;

    @Getter
    private final OffsetDateTime boardingTime;

    public BoardingPass(
            @NonNull String ticketNo,
            @NonNull String flightId,
            int boardingNo,
            @NonNull String seatNo,
            @NonNull OffsetDateTime boardingTime
    ) {
        if (boardingNo <= 0) {
            throw new IllegalArgumentException("Boarding number must be positive: " + boardingNo);
        }
        this.ticketNo = ticketNo;
        this.flightId = flightId;
        this.boardingNo = boardingNo;
        this.seatNo = seatNo.trim().toUpperCase();
        this.boardingTime = boardingTime;
    }

    public static BoardingPass of(
            @NonNull String ticketNo,
            @NonNull String flightId,
            int boardingNo,
            @NonNull String seatNo,
            @NonNull OffsetDateTime boardingTime
    ) {
        return new BoardingPass(ticketNo, flightId, boardingNo, seatNo, boardingTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardingPass that)) return false;
        return Objects.equals(ticketNo, that.getTicketNo()) && Objects.equals(flightId, that.getFlightId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketNo, flightId);
    }

    @Override
    public String toString() {
        return String.format("BoardingPass[Ticket: %s, Flight: %s, Seq: #%d, Seat: %s, Time: %s]",
                ticketNo, flightId, boardingNo, seatNo, boardingTime);
    }
}
