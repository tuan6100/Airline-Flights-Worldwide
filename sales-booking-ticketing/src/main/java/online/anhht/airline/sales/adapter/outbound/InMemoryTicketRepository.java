package online.anhht.airline.sales.adapter.outbound;

import online.anhht.airline.model.Ticket;
import online.anhht.airline.sales.port.outbound.TicketRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTicketRepository implements TicketRepositoryPort {

    private final Map<String, Ticket> tickets = new ConcurrentHashMap<>();

    @Override
    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    @Override
    public Optional<Ticket> findByTicketNo(String ticketNo) {
        if (ticketNo == null) return Optional.empty();
        return Optional.ofNullable(tickets.get(ticketNo.trim()));
    }

    @Override
    public void save(Ticket ticket) {
        tickets.put(ticket.getTicketNo(), ticket);
    }

    @Override
    public void saveAll(List<Ticket> ticketList) {
        for (Ticket t : ticketList) {
            save(t);
        }
    }
}
