package online.anhht.airline.scheduling;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Route;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Strategy implementation using Simulated Annealing optimization algorithm.
 */
public class SimulatedAnnealingScheduler implements FlightSchedulingStrategy {

    private final double initialTemperature;
    private final double coolingRate;
    private final double minTemperature;
    private final int maxIterationsPerTemp;
    private final Random random;

    public SimulatedAnnealingScheduler(double initialTemperature, double coolingRate, double minTemperature, int maxIterationsPerTemp) {
        this.initialTemperature = initialTemperature;
        this.coolingRate = coolingRate;
        this.minTemperature = minTemperature;
        this.maxIterationsPerTemp = maxIterationsPerTemp;
        this.random = new Random(42);
    }

    public SimulatedAnnealingScheduler() {
        this(1000.0, 0.95, 0.1, 50);
    }

    @Override
    public String getAlgorithmName() {
        return "Simulated Annealing";
    }

    @Override
    public SchedulingSolution schedule(SchedulingProblem problem) {
        // Step 1: Generate all flight requirement slots in time horizon
        List<FlightSlot> requiredSlots = generateFlightSlots(problem);
        if (requiredSlots.isEmpty() || problem.getFleet().isEmpty()) {
            return new SchedulingSolution(List.of(), 0.0, true);
        }

        // Map qualified airplanes per slot
        Map<FlightSlot, List<Airplane>> qualifiedPlanesMap = new HashMap<>();
        for (FlightSlot slot : requiredSlots) {
            List<Airplane> qualified = problem.getFleet().stream()
                    .filter(a -> slot.route.isQualifiedAircraft(a))
                    .toList();
            qualifiedPlanesMap.put(slot, qualified);
        }

        // Step 2: Initial solution assignment (Greedy / Round-robin among qualified)
        List<FlightAssignment> currentAssignments = new ArrayList<>();
        int planeIdx = 0;
        for (FlightSlot slot : requiredSlots) {
            List<Airplane> qualified = qualifiedPlanesMap.get(slot);
            if (!qualified.isEmpty()) {
                Airplane assigned = qualified.get(planeIdx % qualified.size());
                planeIdx++;
                currentAssignments.add(new FlightAssignment(
                        slot.slotId, slot.route, assigned, slot.departure, slot.arrival
                ));
            }
        }

        double currentEnergy = calculateCost(currentAssignments);
        List<FlightAssignment> bestAssignments = new ArrayList<>(currentAssignments);
        double bestEnergy = currentEnergy;

        double temp = initialTemperature;
        while (temp > minTemperature && !currentAssignments.isEmpty()) {
            for (int i = 0; i < maxIterationsPerTemp; i++) {
                // Perturb: choose a random assignment and assign another qualified aircraft
                int changeIdx = random.nextInt(currentAssignments.size());
                FlightAssignment oldAssignment = currentAssignments.get(changeIdx);
                FlightSlot correspondingSlot = requiredSlots.stream()
                        .filter(s -> s.slotId.equals(oldAssignment.flightId()))
                        .findFirst().orElse(null);

                if (correspondingSlot == null) continue;
                List<Airplane> qualified = qualifiedPlanesMap.get(correspondingSlot);
                if (qualified == null || qualified.size() <= 1) continue;

                Airplane newAirplane = qualified.get(random.nextInt(qualified.size()));
                FlightAssignment newAssignment = new FlightAssignment(
                        oldAssignment.flightId(), oldAssignment.route(), newAirplane,
                        oldAssignment.scheduledDeparture(), oldAssignment.scheduledArrival()
                );

                List<FlightAssignment> neighborAssignments = new ArrayList<>(currentAssignments);
                neighborAssignments.set(changeIdx, newAssignment);

                double neighborEnergy = calculateCost(neighborAssignments);
                double delta = neighborEnergy - currentEnergy;

                if (delta < 0 || Math.exp(-delta / temp) > random.nextDouble()) {
                    currentAssignments = neighborAssignments;
                    currentEnergy = neighborEnergy;

                    if (currentEnergy < bestEnergy) {
                        bestAssignments = new ArrayList<>(currentAssignments);
                        bestEnergy = currentEnergy;
                    }
                }
            }
            temp *= coolingRate;
        }

        boolean feasible = countOverlaps(bestAssignments) == 0;
        double fitnessScore = Math.max(0.0, 1000.0 - bestEnergy);
        return new SchedulingSolution(bestAssignments, fitnessScore, feasible);
    }

    private double calculateCost(List<FlightAssignment> assignments) {
        int overlaps = countOverlaps(assignments);
        // Cost = 100 * overlaps + fleet utilization variance
        Map<Airplane, Integer> usage = new HashMap<>();
        for (FlightAssignment fa : assignments) {
            usage.put(fa.airplane(), usage.getOrDefault(fa.airplane(), 0) + 1);
        }
        double variance = 0.0;
        if (!usage.isEmpty()) {
            double avg = (double) assignments.size() / usage.size();
            for (int count : usage.values()) {
                variance += Math.pow(count - avg, 2);
            }
        }
        return (overlaps * 100.0) + variance;
    }

    private int countOverlaps(List<FlightAssignment> assignments) {
        int overlaps = 0;
        for (int i = 0; i < assignments.size(); i++) {
            for (int j = i + 1; j < assignments.size(); j++) {
                if (assignments.get(i).overlapsWith(assignments.get(j))) {
                    overlaps++;
                }
            }
        }
        return overlaps;
    }

    private List<FlightSlot> generateFlightSlots(SchedulingProblem problem) {
        List<FlightSlot> slots = new ArrayList<>();
        LocalDate cur = problem.getStartDate();
        int counter = 1;
        while (!cur.isAfter(problem.getEndDate())) {
            for (Route route : problem.getRoutes()) {
                OffsetDateTime dep = cur.atTime(route.getScheduledDepartureTime()).atOffset(ZoneOffset.UTC);
                if (route.operatesOn(cur.getDayOfWeek(), dep)) {
                    OffsetDateTime arr = dep.plus(route.getScheduledDuration());
                    String slotId = String.format("FL-%s-%s-%03d", route.getFlightNo(), cur, counter++);
                    slots.add(new FlightSlot(slotId, route, dep, arr));
                }
            }
            cur = cur.plusDays(1);
        }
        return slots;
    }

    private record FlightSlot(String slotId, Route route, OffsetDateTime departure, OffsetDateTime arrival) {}
}
