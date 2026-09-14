package online.anhht.airline.operations.port.inbound;

import online.anhht.airline.model.Flight;
import online.anhht.airline.model.FlightStatus;
import online.anhht.airline.model.Route;
import online.anhht.airline.operations.ScheduleDeviationAnalyzer.DeviationReport;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightOperationsUseCase {
    List<Flight> realizeFlights60DaysAhead(Route route, LocalDate today);
    List<Flight> realizeFlightsForHorizon(Route route, LocalDate startDate, int daysAhead);

    List<Flight> getAllFlights();
    Optional<Flight> getFlightById(String flightId);

    Flight updateFlightToOnTime(String flightId);
    Flight updateFlightToDelayed(String flightId, OffsetDateTime expectedDeparture, String reason);
    Flight updateFlightToBoarding(String flightId);
    Flight updateFlightToDeparted(String flightId, OffsetDateTime actualDeparture);
    Flight updateFlightToArrived(String flightId, OffsetDateTime actualArrival);
    Flight cancelFlight(String flightId, String reason);

    DeviationReport getScheduleDeviationReport(String flightId);
}
