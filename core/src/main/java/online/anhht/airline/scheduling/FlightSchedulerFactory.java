package online.anhht.airline.scheduling;

/**
 * Abstract factory for creating flight scheduling optimization strategies.
 */
public interface FlightSchedulerFactory {

    /**
     * Creates a simulated annealing scheduling strategy with default parameters.
     */
    FlightSchedulingStrategy createSimulatedAnnealingScheduler();

    /**
     * Creates a simulated annealing scheduling strategy with custom parameters.
     */
    FlightSchedulingStrategy createSimulatedAnnealingScheduler(
            double initialTemperature,
            double coolingRate,
            double minTemperature,
            int maxIterationsPerTemp
    );

    /**
     * Creates a genetic algorithm scheduling strategy with default parameters.
     */
    FlightSchedulingStrategy createGeneticAlgorithmScheduler();

    /**
     * Creates a genetic algorithm scheduling strategy with custom parameters.
     */
    FlightSchedulingStrategy createGeneticAlgorithmScheduler(
            int populationSize,
            int maxGenerations,
            double mutationRate,
            double crossoverRate,
            int tournamentSize
    );

    /**
     * Returns the singleton instance of {@link FlightSchedulerFactory}.
     */
    static FlightSchedulerFactory getInstance() {
        return DefaultFlightSchedulerFactory.INSTANCE;
    }
}
