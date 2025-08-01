package online.anhht.airline.scheduling;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the output of a flight scheduling algorithm execution.
 */
public class SchedulingSolution {

    private final List<FlightAssignment> assignments;
    private final double fitnessScore;
    private final boolean feasible;

    public SchedulingSolution(List<FlightAssignment> assignments, double fitnessScore, boolean feasible) {
        Objects.requireNonNull(assignments, "Assignments must not be null");
        this.assignments = List.copyOf(assignments);
        this.fitnessScore = fitnessScore;
        this.feasible = feasible;
    }

    public List<FlightAssignment> getAssignments() {
        return assignments;
    }

    public double getFitnessScore() {
        return fitnessScore;
    }

    public boolean isFeasible() {
        return feasible;
    }

    public int getScheduledFlightCount() {
        return assignments.size();
    }
}
