package online.anhht.airline.checkin.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataBoardingPassRepository extends JpaRepository<BoardingPassEntity, BoardingPassId> {
    List<BoardingPassEntity> findByFlightId(String flightId);
}
