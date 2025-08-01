package online.anhht.airline.scheduling;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Flight Scheduling Strategy Pattern Unit Tests")
class SchedulingStrategyTest {

    private SchedulingProblem createSampleProblem() {
        Airport jfk = Airport.of("JFK", "JFK", "New York", "USA", new Coordinates(40.64, -73.77), ZoneId.of("America/New_York"), 50);
        Airport lhr = Airport.of("LHR", "Heathrow", "London", "UK", new Coordinates(51.47, -0.45), ZoneId.of("Europe/London"), 50);

        TemporalValidityRange validity = new TemporalValidityRange(
                OffsetDateTime.of(2026, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC),
                OffsetDateTime.of(2026, 12, 31, 23, 59, 59, 0, ZoneOffset.UTC)
        );

        Route route1 = Route.of("BA101", jfk, lhr, Set.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY), LocalTime.of(8, 0), Duration.ofHours(7), validity);
        Route route2 = Route.of("BA102", lhr, jfk, Set.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY), LocalTime.of(12, 0), Duration.ofHours(8), validity);

        Seat seat = Seat.of("1A", FareCondition.ECONOMY, "773");
        CabinLayout layout = CabinLayout.of("773", List.of(seat));
        Airplane plane1 = Airplane.of("AIRCRAFT-01", "Boeing 777", 10000, 900, layout);
        Airplane plane2 = Airplane.of("AIRCRAFT-02", "Boeing 777", 10000, 900, layout);

        return new SchedulingProblem(
                List.of(route1, route2),
                List.of(plane1, plane2),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 7)
        );
    }

    @Test
    @DisplayName("Should generate schedule using Simulated Annealing algorithm via Factory")
    void testSimulatedAnnealingScheduler() {
        SchedulingProblem problem = createSampleProblem();
        FlightSchedulingStrategy saScheduler = FlightSchedulerFactory.getInstance().createSimulatedAnnealingScheduler();

        assertEquals("Simulated Annealing", saScheduler.getAlgorithmName());
        SchedulingSolution solution = saScheduler.schedule(problem);

        assertNotNull(solution);
        assertTrue(solution.getScheduledFlightCount() > 0);
        assertTrue(solution.getFitnessScore() > 0.0);
    }

    @Test
    @DisplayName("Should generate schedule using Genetic Algorithm via Factory")
    void testGeneticAlgorithmScheduler() {
        SchedulingProblem problem = createSampleProblem();
        FlightSchedulingStrategy gaScheduler = FlightSchedulerFactory.getInstance().createGeneticAlgorithmScheduler();

        assertEquals("Genetic Algorithm", gaScheduler.getAlgorithmName());
        SchedulingSolution solution = gaScheduler.schedule(problem);

        assertNotNull(solution);
        assertTrue(solution.getScheduledFlightCount() > 0);
        assertTrue(solution.getFitnessScore() > 0.0);
    }
}
