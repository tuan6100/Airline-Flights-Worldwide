package online.anhht.airline.sales.adapter.outbound.rest;

import online.anhht.airline.sales.port.outbound.InventoryServiceClientPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RestInventoryClientAdapter implements InventoryServiceClientPort {

    private final RestClient restClient;
    private final Map<String, Boolean> reservedSeats = new ConcurrentHashMap<>();

    public RestInventoryClientAdapter(
            @Value("${services.inventory.url:http://localhost:8002}") String baseUrl
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public boolean validateSeatAvailability(String aircraftCode, String seatNo) {
        try {
            Boolean exists = restClient.get()
                    .uri("/api/v1/inventory/layouts/{aircraftCode}/seats/validate?seatNo={seatNo}", aircraftCode, seatNo)
                    .retrieve()
                    .body(Boolean.class);
            return Boolean.TRUE.equals(exists);
        } catch (Exception ex) {
            return seatNo != null && !seatNo.isBlank();
        }
    }

    @Override
    public synchronized boolean reserveSeat(String flightId, String aircraftCode, String seatNo) {
        String key = flightId + ":" + seatNo;
        if (reservedSeats.containsKey(key)) {
            return false;
        }
        if (validateSeatAvailability(aircraftCode, seatNo)) {
            reservedSeats.put(key, true);
            return true;
        }
        return false;
    }

    @Override
    public synchronized void releaseSeatReservation(String flightId, String aircraftCode, String seatNo) {
        String key = flightId + ":" + seatNo;
        reservedSeats.remove(key);
    }
}
