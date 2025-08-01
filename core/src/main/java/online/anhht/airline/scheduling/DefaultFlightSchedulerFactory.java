package online.anhht.airline.scheduling;

/**
 * Singleton implementation of {@link FlightSchedulerFactory}.
 */
public enum DefaultFlightSchedulerFactory implements FlightSchedulerFactory {
    INSTANCE;

    @Override
    public FlightSchedulingStrategy createSimulatedAnnealingScheduler() {
        return new SimulatedAnnealingScheduler();
    }

    @Override
    public FlightSchedulingStrategy createSimulatedAnnealingScheduler(
            double initialTemperature,
            double coolingRate,
            double minTemperature,
            int maxIterationsPerTemp
    ) {
        return new SimulatedAnnealingScheduler(initialTemperature, coolingRate, minTemperature, maxIterationsPerTemp);
    }

    @Override
    public FlightSchedulingStrategy createGeneticAlgorithmScheduler() {
        return new GeneticAlgorithmScheduler();
    }

    @Override
    public FlightSchedulingStrategy createGeneticAlgorithmScheduler(
            int populationSize,
            int maxGenerations,
            double mutationRate,
            double crossoverRate,
            int tournamentSize
    ) {
        return new GeneticAlgorithmScheduler(populationSize, maxGenerations, mutationRate, crossoverRate, tournamentSize);
    }
}
