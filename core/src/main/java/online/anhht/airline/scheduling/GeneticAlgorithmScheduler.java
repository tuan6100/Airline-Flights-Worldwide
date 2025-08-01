package online.anhht.airline.scheduling;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Route;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Strategy implementation using Genetic Algorithm optimization.
 */
public class GeneticAlgorithmScheduler implements FlightSchedulingStrategy {

    private final int populationSize;
    private final int maxGenerations;
    private final double mutationRate;
    private final double crossoverRate;
    private final int tournamentSize;
    private final Random random;

    public GeneticAlgorithmScheduler(
            int populationSize,
            int maxGenerations,
            double mutationRate,
            double crossoverRate,
            int tournamentSize
    ) {
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.tournamentSize = tournamentSize;
        this.random = new Random(12345);
    }

    public GeneticAlgorithmScheduler() {
        this(40, 60, 0.05, 0.85, 4);
    }

    @Override
    public String getAlgorithmName() {
        return "Genetic Algorithm";
    }

    @Override
    public SchedulingSolution schedule(SchedulingProblem problem) {
        List<FlightSlot> slots = generateFlightSlots(problem);
        if (slots.isEmpty() || problem.getFleet().isEmpty()) {
            return new SchedulingSolution(List.of(), 0.0, true);
        }

        Map<FlightSlot, List<Airplane>> qualifiedPlanesMap = new HashMap<>();
        for (FlightSlot slot : slots) {
            List<Airplane> qualified = problem.getFleet().stream()
                    .filter(a -> slot.route.isQualifiedAircraft(a))
                    .toList();
            qualifiedPlanesMap.put(slot, qualified.isEmpty() ? problem.getFleet() : qualified);
        }

        // Initialize population
        List<Chromosome> population = new ArrayList<>();
        for (int p = 0; p < populationSize; p++) {
            List<Airplane> genes = new ArrayList<>();
            for (FlightSlot slot : slots) {
                List<Airplane> qualified = qualifiedPlanesMap.get(slot);
                genes.add(qualified.get(random.nextInt(qualified.size())));
            }
            population.add(new Chromosome(genes, evaluateFitness(genes, slots)));
        }

        // Evolution loop
        for (int gen = 0; gen < maxGenerations; gen++) {
            population.sort(Comparator.comparingDouble(Chromosome::fitness).reversed());
            List<Chromosome> nextGen = new ArrayList<>();

            // Elitism: keep top 2
            nextGen.add(population.get(0));
            if (population.size() > 1) {
                nextGen.add(population.get(1));
            }

            while (nextGen.size() < populationSize) {
                Chromosome parent1 = tournamentSelect(population);
                Chromosome parent2 = tournamentSelect(population);

                List<Airplane> childGenes;
                if (random.nextDouble() < crossoverRate) {
                    childGenes = crossover(parent1.genes, parent2.genes);
                } else {
                    childGenes = new ArrayList<>(parent1.genes);
                }

                mutate(childGenes, slots, qualifiedPlanesMap);
                nextGen.add(new Chromosome(childGenes, evaluateFitness(childGenes, slots)));
            }

            population = nextGen;
        }

        population.sort(Comparator.comparingDouble(Chromosome::fitness).reversed());
        Chromosome best = population.get(0);

        List<FlightAssignment> bestAssignments = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            FlightSlot slot = slots.get(i);
            Airplane plane = best.genes.get(i);
            bestAssignments.add(new FlightAssignment(slot.slotId, slot.route, plane, slot.departure, slot.arrival));
        }

        boolean feasible = countOverlaps(bestAssignments) == 0;
        return new SchedulingSolution(bestAssignments, best.fitness, feasible);
    }

    private Chromosome tournamentSelect(List<Chromosome> pop) {
        Chromosome best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome c = pop.get(random.nextInt(pop.size()));
            if (best == null || c.fitness > best.fitness) {
                best = c;
            }
        }
        return best;
    }

    private List<Airplane> crossover(List<Airplane> g1, List<Airplane> g2) {
        int point = random.nextInt(g1.size());
        List<Airplane> child = new ArrayList<>();
        for (int i = 0; i < g1.size(); i++) {
            child.add(i < point ? g1.get(i) : g2.get(i));
        }
        return child;
    }

    private void mutate(List<Airplane> genes, List<FlightSlot> slots, Map<FlightSlot, List<Airplane>> qualifiedMap) {
        for (int i = 0; i < genes.size(); i++) {
            if (random.nextDouble() < mutationRate) {
                List<Airplane> qualified = qualifiedMap.get(slots.get(i));
                if (!qualified.isEmpty()) {
                    genes.set(i, qualified.get(random.nextInt(qualified.size())));
                }
            }
        }
    }

    private double evaluateFitness(List<Airplane> genes, List<FlightSlot> slots) {
        List<FlightAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < slots.size(); i++) {
            FlightSlot s = slots.get(i);
            assignments.add(new FlightAssignment(s.slotId, s.route, genes.get(i), s.departure, s.arrival));
        }
        int overlaps = countOverlaps(assignments);
        return Math.max(0.0, 1000.0 - (overlaps * 150.0));
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
                    String slotId = String.format("GA-%s-%s-%03d", route.getFlightNo(), cur, counter++);
                    slots.add(new FlightSlot(slotId, route, dep, arr));
                }
            }
            cur = cur.plusDays(1);
        }
        return slots;
    }

    private record Chromosome(List<Airplane> genes, double fitness) {}
    private record FlightSlot(String slotId, Route route, OffsetDateTime departure, OffsetDateTime arrival) {}
}
