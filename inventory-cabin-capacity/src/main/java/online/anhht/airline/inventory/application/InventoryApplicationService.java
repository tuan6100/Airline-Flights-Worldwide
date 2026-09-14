package online.anhht.airline.inventory.application;

import online.anhht.airline.inventory.port.inbound.InventoryUseCase;
import online.anhht.airline.inventory.port.outbound.CabinLayoutRepositoryPort;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Seat;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryApplicationService implements InventoryUseCase {

    private final CabinLayoutRepositoryPort cabinLayoutRepo;

    public InventoryApplicationService(CabinLayoutRepositoryPort cabinLayoutRepo) {
        this.cabinLayoutRepo = cabinLayoutRepo;
    }

    @Override
    public List<CabinLayout> getAllCabinLayouts() {
        return cabinLayoutRepo.findAll();
    }

    @Override
    public Optional<CabinLayout> getCabinLayout(String aircraftCode) {
        return cabinLayoutRepo.findByAircraftCode(aircraftCode);
    }

    @Override
    public Map<FareCondition, Integer> getCapacityBreakdown(String aircraftCode) {
        CabinLayout layout = getExistingLayout(aircraftCode);
        Map<FareCondition, Integer> breakdown = new EnumMap<>(FareCondition.class);
        for (FareCondition fc : FareCondition.values()) {
            breakdown.put(fc, layout.getCapacity(fc));
        }
        return breakdown;
    }

    @Override
    public int getTotalCapacity(String aircraftCode) {
        return getExistingLayout(aircraftCode).getTotalCapacity();
    }

    @Override
    public List<Seat> getSeatsForAircraft(String aircraftCode) {
        return getExistingLayout(aircraftCode).getSeats();
    }

    @Override
    public boolean hasSeat(String aircraftCode, String seatNo) {
        return getExistingLayout(aircraftCode).hasSeat(seatNo);
    }

    private CabinLayout getExistingLayout(String aircraftCode) {
        return cabinLayoutRepo.findByAircraftCode(aircraftCode)
                .orElseThrow(() -> new IllegalArgumentException("Cabin layout not found for aircraft: " + aircraftCode));
    }
}
