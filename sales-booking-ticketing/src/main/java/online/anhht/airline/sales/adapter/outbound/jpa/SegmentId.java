package online.anhht.airline.sales.adapter.outbound.jpa;

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
public class SegmentId implements Serializable {
    private String ticketNo;
    private String flightId;
}
