package online.anhht.airline.sales.port.outbound;

import online.anhht.airline.model.Ticket;

import java.util.List;
import java.util.Optional;

public interface TicketRepositoryPort {
    List<Ticket> findAll();
    Optional<Ticket> findByTicketNo(String ticketNo);
    void save(Ticket ticket);
    void saveAll(List<Ticket> tickets);
}
