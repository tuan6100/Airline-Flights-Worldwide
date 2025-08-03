package online.anhht.airline.planning.adapter.outbound.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Seat;

import java.util.List;

@Entity
@Table(name = "airplanes_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirplaneEntity {

    @Id
    @Column(name = "airplane_code", columnDefinition = "char(3)")
    private String id;

    @Column(name = "model", columnDefinition = "text")
    private String model;

    @Column(name = "range")
    private int range;

    @Column(name = "speed")
    private int speed;

    public Airplane toDomain() {
        CabinLayout layout = CabinLayout.of(
                id,
                List.of(
                        Seat.of("1A", FareCondition.BUSINESS, id),
                        Seat.of("2A", FareCondition.ECONOMY, id)
                )
        );
        return Airplane.of(id, model != null ? model : id, range > 0 ? range : 5000, speed > 0 ? speed : 850, layout);
    }

    public static AirplaneEntity fromDomain(Airplane airplane) {
        return new AirplaneEntity(airplane.getId(), airplane.getModel(), airplane.getRangeKm(), airplane.getCruisingSpeedKmH());
    }
}
