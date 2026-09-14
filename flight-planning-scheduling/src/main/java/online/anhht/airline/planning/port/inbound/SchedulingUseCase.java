package online.anhht.airline.planning.port.inbound;

import online.anhht.airline.scheduling.SchedulingSolution;

import java.time.LocalDate;

public interface SchedulingUseCase {
    SchedulingSolution generateOptimizedSchedule(LocalDate startDate, LocalDate endDate, String algorithm);
}
