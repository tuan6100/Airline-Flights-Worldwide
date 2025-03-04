package online.anhht.airline.inventory.port.inbound;

import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Seat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface InventoryUseCase {
    List<CabinLayout> getAllCabinLayouts();
    Optional<CabinLayout> getCabinLayout(String aircraftCode);
    Map<FareCondition, Integer> getCapacityBreakdown(String aircraftCode);
    int getTotalCapacity(String aircraftCode);
    List<Seat> getSeatsForAircraft(String aircraftCode);
    boolean hasSeat(String aircraftCode, String seatNo);
}
