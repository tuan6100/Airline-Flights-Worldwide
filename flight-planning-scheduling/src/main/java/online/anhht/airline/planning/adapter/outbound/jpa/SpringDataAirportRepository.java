package online.anhht.airline.planning.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAirportRepository extends JpaRepository<AirportEntity, String> {
}
