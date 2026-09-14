package online.anhht.airline.sales.adapter.outbound.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataTicketRepository extends JpaRepository<TicketEntity, String> {
    List<TicketEntity> findByBookRef(String bookRef);
}
