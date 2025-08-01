package online.anhht.airline.scheduling;

/**
 * Strategy pattern interface for flight scheduling optimization algorithms.
 */
public interface FlightSchedulingStrategy {

    String getAlgorithmName();

    SchedulingSolution schedule(SchedulingProblem problem);
}
