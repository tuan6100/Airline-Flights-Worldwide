package online.anhht.airline.inventory.adapter.inbound;

import online.anhht.airline.inventory.port.inbound.InventoryUseCase;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Seat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventory/layouts")
public class CabinLayoutController {

    private final InventoryUseCase inventoryUseCase;

    public CabinLayoutController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @GetMapping
    public ResponseEntity<List<CabinLayoutSummary>> getAllLayouts() {
        List<CabinLayoutSummary> list = inventoryUseCase.getAllCabinLayouts().stream()
                .map(CabinLayoutSummary::fromDomain)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{aircraftCode}")
    public ResponseEntity<CabinLayoutDetail> getCabinLayout(@PathVariable String aircraftCode) {
        return inventoryUseCase.getCabinLayout(aircraftCode)
                .map(CabinLayoutDetail::fromDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{aircraftCode}/seats/validate")
    public ResponseEntity<Map<String, Object>> validateSeat(
            @PathVariable String aircraftCode,
            @RequestParam String seatNo
    ) {
        boolean exists = inventoryUseCase.hasSeat(aircraftCode, seatNo);
        return ResponseEntity.ok(Map.of(
                "aircraftCode", aircraftCode.toUpperCase(),
                "seatNo", seatNo.toUpperCase(),
                "isValidSeat", exists
        ));
    }

    public record CabinLayoutSummary(String aircraftCode, int totalCapacity) {
        public static CabinLayoutSummary fromDomain(CabinLayout cl) {
            return new CabinLayoutSummary(cl.getAircraftCode(), cl.getTotalCapacity());
        }
    }

    public record CabinLayoutDetail(
            String aircraftCode,
            int totalCapacity,
            List<SeatDto> seats
    ) {
        public static CabinLayoutDetail fromDomain(CabinLayout cl) {
            List<SeatDto> seatDtos = cl.getSeats().stream()
                    .map(s -> new SeatDto(s.getSeatNo(), s.getFareCondition().name()))
                    .toList();
            return new CabinLayoutDetail(cl.getAircraftCode(), cl.getTotalCapacity(), seatDtos);
        }
    }

    public record SeatDto(String seatNo, String fareCondition) {}
}
