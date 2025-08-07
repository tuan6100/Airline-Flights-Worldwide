package online.anhht.airline.sales.port.outbound;

public interface InventoryServiceClientPort {

    boolean validateSeatAvailability(String aircraftCode, String seatNo);
    boolean reserveSeat(String flightId, String aircraftCode, String seatNo);
    void releaseSeatReservation(String flightId, String aircraftCode, String seatNo);
}
