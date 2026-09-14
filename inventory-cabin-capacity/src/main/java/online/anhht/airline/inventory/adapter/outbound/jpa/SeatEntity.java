package online.anhht.airline.inventory.adapter.outbound.jpa;

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
import online.anhht.airline.model.Seat;

@Entity
@Table(name = "seats")
@IdClass(SeatId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatEntity {

    @Id
    @Column(name = "airplane_code", columnDefinition = "char(3)")
    private String airplaneCode;

    @Id
    @Column(name = "seat_no")
    private String seatNo;

    @Column(name = "fare_conditions")
    private String fareConditions;

    public Seat toDomain() {
        FareCondition condition = FareCondition.ECONOMY;
        if (fareConditions != null) {
            try {
                condition = FareCondition.valueOf(fareConditions.toUpperCase().trim());
            } catch (Exception ignored) {
            }
        }
        return Seat.of(seatNo, condition, airplaneCode);
    }

    public static SeatEntity fromDomain(Seat seat) {
        return new SeatEntity(seat.getAircraftCode(), seat.getSeatNo(), seat.getFareCondition().name());
    }
}
