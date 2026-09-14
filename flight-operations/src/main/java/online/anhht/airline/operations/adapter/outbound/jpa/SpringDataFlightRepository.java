package online.anhht.airline.operations.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataFlightRepository extends JpaRepository<FlightEntity, String> {
}
