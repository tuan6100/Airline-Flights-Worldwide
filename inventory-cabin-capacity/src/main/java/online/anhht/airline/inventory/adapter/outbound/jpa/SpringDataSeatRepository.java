package online.anhht.airline.inventory.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataSeatRepository extends JpaRepository<SeatEntity, SeatId> {
    List<SeatEntity> findByAirplaneCode(String airplaneCode);
}
