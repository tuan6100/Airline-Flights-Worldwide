package online.anhht.airline.planning.adapter.inbound;

import online.anhht.airline.planning.port.inbound.SchedulingUseCase;
import online.anhht.airline.scheduling.FlightAssignment;
import online.anhht.airline.scheduling.SchedulingSolution;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final SchedulingUseCase schedulingUseCase;

    public ScheduleController(SchedulingUseCase schedulingUseCase) {
        this.schedulingUseCase = schedulingUseCase;
    }

    @GetMapping("/optimize")
    public ResponseEntity<ScheduleOptimizationResponse> optimizeSchedule(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "SA") String algorithm
    ) {
        SchedulingSolution solution = schedulingUseCase.generateOptimizedSchedule(startDate, endDate, algorithm);
        List<FlightAssignmentResponse> assignments = solution.getAssignments().stream()
                .map(FlightAssignmentResponse::fromDomain)
                .toList();

        ScheduleOptimizationResponse response = new ScheduleOptimizationResponse(
                algorithm.toUpperCase(),
                startDate.toString(),
                endDate.toString(),
                solution.getScheduledFlightCount(),
                solution.getFitnessScore(),
                solution.isFeasible(),
                assignments
        );

        return ResponseEntity.ok(response);
    }

    public record ScheduleOptimizationResponse(
            String algorithm,
            String startDate,
            String endDate,
            int scheduledFlightCount,
            double fitnessScore,
            boolean isFeasible,
            List<FlightAssignmentResponse> assignments
    ) {}

    public record FlightAssignmentResponse(
            String flightId,
            String flightNo,
            String airplaneId,
            String airplaneModel,
            String scheduledDeparture,
            String scheduledArrival
    ) {
        public static FlightAssignmentResponse fromDomain(FlightAssignment fa) {
            return new FlightAssignmentResponse(
                    fa.flightId(),
                    fa.route().getFlightNo(),
                    fa.airplane().getId(),
                    fa.airplane().getModel(),
                    fa.scheduledDeparture().toString(),
                    fa.scheduledArrival().toString()
            );
        }
    }
}
