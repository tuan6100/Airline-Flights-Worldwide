package online.anhht.airline.sales.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataBookingRepository extends JpaRepository<BookingEntity, String> {
}
