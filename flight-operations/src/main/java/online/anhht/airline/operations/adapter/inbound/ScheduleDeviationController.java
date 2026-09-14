package online.anhht.airline.operations.adapter.inbound;

import online.anhht.airline.operations.ScheduleDeviationAnalyzer.DeviationReport;
import online.anhht.airline.operations.port.inbound.FlightOperationsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/operations/deviations")
public class ScheduleDeviationController {

    private final FlightOperationsUseCase operationsUseCase;

    public ScheduleDeviationController(FlightOperationsUseCase operationsUseCase) {
        this.operationsUseCase = operationsUseCase;
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<DeviationReport> getDeviationReport(@PathVariable String flightId) {
        DeviationReport report = operationsUseCase.getScheduleDeviationReport(flightId);
        return ResponseEntity.ok(report);
    }
}
