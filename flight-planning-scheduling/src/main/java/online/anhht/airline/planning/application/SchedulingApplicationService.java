package online.anhht.airline.planning.application;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Route;
import online.anhht.airline.planning.port.inbound.SchedulingUseCase;
import online.anhht.airline.planning.port.outbound.FleetRepositoryPort;
import online.anhht.airline.planning.port.outbound.RouteRepositoryPort;
import online.anhht.airline.scheduling.FlightSchedulingStrategy;
import online.anhht.airline.scheduling.GeneticAlgorithmScheduler;
import online.anhht.airline.scheduling.SchedulingProblem;
import online.anhht.airline.scheduling.SchedulingSolution;
import online.anhht.airline.scheduling.SimulatedAnnealingScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class SchedulingApplicationService implements SchedulingUseCase {

    private final RouteRepositoryPort routeRepo;
    private final FleetRepositoryPort fleetRepo;
    private final Map<String, FlightSchedulingStrategy> strategyMap;

    public SchedulingApplicationService(RouteRepositoryPort routeRepo, FleetRepositoryPort fleetRepo) {
        this.routeRepo = routeRepo;
        this.fleetRepo = fleetRepo;
        this.strategyMap = Map.of(
                "SA", new SimulatedAnnealingScheduler(),
                "SIMULATED_ANNEALING", new SimulatedAnnealingScheduler(),
                "GA", new GeneticAlgorithmScheduler(),
                "GENETIC_ALGORITHM", new GeneticAlgorithmScheduler()
        );
    }

    @Override
    public SchedulingSolution generateOptimizedSchedule(LocalDate startDate, LocalDate endDate, String algorithm) {
        String key = algorithm != null ? algorithm.trim().toUpperCase() : "SA";
        FlightSchedulingStrategy strategy = strategyMap.getOrDefault(key, new SimulatedAnnealingScheduler());

        List<Route> routes = routeRepo.findAll();
        List<Airplane> fleet = fleetRepo.findAll();

        SchedulingProblem problem = new SchedulingProblem(routes, fleet, startDate, endDate);
        return strategy.schedule(problem);
    }
}
