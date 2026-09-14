package online.anhht.airline.inventory.adapter.outbound.jpa;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SeatId implements Serializable {
    private String airplaneCode;
    private String seatNo;
}
