package online.anhht.airline.inventory.adapter.inbound;

import online.anhht.airline.inventory.port.inbound.InventoryUseCase;
import online.anhht.airline.model.FareCondition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory/capacity")
public class CapacityController {

    private final InventoryUseCase inventoryUseCase;

    public CapacityController(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    @GetMapping("/{aircraftCode}")
    public ResponseEntity<CapacityResponse> getCapacity(@PathVariable String aircraftCode) {
        int total = inventoryUseCase.getTotalCapacity(aircraftCode);
        Map<FareCondition, Integer> breakdown = inventoryUseCase.getCapacityBreakdown(aircraftCode);

        Map<String, Integer> stringMap = breakdown.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));

        return ResponseEntity.ok(new CapacityResponse(aircraftCode.toUpperCase(), total, stringMap));
    }

    public record CapacityResponse(
            String aircraftCode,
            int totalCapacity,
            Map<String, Integer> capacityByTravelClass
    ) {}
}
