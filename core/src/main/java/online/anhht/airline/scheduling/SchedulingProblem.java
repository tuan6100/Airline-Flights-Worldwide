package online.anhht.airline.scheduling;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.Route;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the scheduling input problem: routes to service, available fleet, and scheduling period.
 */
public class SchedulingProblem {

    private final List<Route> routes;
    private final List<Airplane> fleet;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public SchedulingProblem(
            List<Route> routes,
            List<Airplane> fleet,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Objects.requireNonNull(routes, "Routes must not be null");
        Objects.requireNonNull(fleet, "Fleet must not be null");
        Objects.requireNonNull(startDate, "Start date must not be null");
        Objects.requireNonNull(endDate, "End date must not be null");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }
        this.routes = List.copyOf(routes);
        this.fleet = List.copyOf(fleet);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public List<Airplane> getFleet() {
        return fleet;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
