package online.anhht.airline.operations.application;

import online.anhht.airline.events.FlightScheduledEvent;
import online.anhht.airline.events.FlightStatusChangedEvent;
import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.FlightStatus;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.operations.FlightRealizer;
import online.anhht.airline.operations.ScheduleDeviationAnalyzer;
import online.anhht.airline.operations.ScheduleDeviationAnalyzer.DeviationReport;
import online.anhht.airline.operations.port.inbound.FlightOperationsUseCase;
import online.anhht.airline.operations.port.outbound.EventPublisherPort;
import online.anhht.airline.operations.port.outbound.FlightRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightOperationsApplicationService implements FlightOperationsUseCase {

    private final FlightRepositoryPort flightRepo;
    private final EventPublisherPort eventPublisher;
    private final FlightRealizer flightRealizer = FlightRealizer.of();
    private final ScheduleDeviationAnalyzer deviationAnalyzer = ScheduleDeviationAnalyzer.of();

    public FlightOperationsApplicationService(FlightRepositoryPort flightRepo, EventPublisherPort eventPublisher) {
        this.flightRepo = flightRepo;
        this.eventPublisher = eventPublisher;
    }

    private Airplane createDefaultQualifiedAirplane() {
        Seat s1 = Seat.of("1A", FareCondition.BUSINESS, "320");
        Seat s2 = Seat.of("2A", FareCondition.ECONOMY, "320");
        CabinLayout layout = CabinLayout.of("320", List.of(s1, s2));
        return Airplane.of("DEFAULT-PLANE", "Airbus A320", 8000, 850, layout);
    }

    @Override
    public List<Flight> realizeFlights60DaysAhead(Route route, LocalDate today) {
        Airplane plane = createDefaultQualifiedAirplane();
        List<Flight> flights = flightRealizer.realizeFlights60DaysAdvance(route, plane, today);
        flightRepo.saveAll(flights);
        for (Flight f : flights) {
            eventPublisher.publish(new FlightScheduledEvent(
                    f.getFlightId(), f.getFlightNo(),
                    f.getRoute().getDepartureAirport().getAirportCode(),
                    f.getRoute().getArrivalAirport().getAirportCode(),
                    f.getScheduledDeparture(), f.getScheduledArrival()
            ));
        }
        return flights;
    }

    @Override
    public List<Flight> realizeFlightsForHorizon(Route route, LocalDate startDate, int daysAhead) {
        Airplane plane = createDefaultQualifiedAirplane();
        List<Flight> flights = flightRealizer.realizeFlightsForHorizon(route, plane, startDate, daysAhead);
        flightRepo.saveAll(flights);
        return flights;
    }

    @Override
    public List<Flight> getAllFlights() {
        return flightRepo.findAll();
    }

    @Override
    public Optional<Flight> getFlightById(String flightId) {
        return flightRepo.findById(flightId);
    }

    @Override
    public Flight updateFlightToOnTime(String flightId) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.markOnTime();
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), null));
        return flight;
    }

    @Override
    public Flight updateFlightToDelayed(String flightId, OffsetDateTime expectedDeparture, String reason) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.markDelayed(expectedDeparture, reason);
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), reason));
        return flight;
    }

    @Override
    public Flight updateFlightToBoarding(String flightId) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.startBoarding();
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), null));
        return flight;
    }

    @Override
    public Flight updateFlightToDeparted(String flightId, OffsetDateTime actualDeparture) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.depart(actualDeparture);
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), "Departed"));
        return flight;
    }

    @Override
    public Flight updateFlightToArrived(String flightId, OffsetDateTime actualArrival) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.arrive(actualArrival);
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), "Arrived"));
        return flight;
    }

    @Override
    public Flight cancelFlight(String flightId, String reason) {
        Flight flight = getExistingFlight(flightId);
        FlightStatus prev = flight.getStatus();
        flight.cancel(reason);
        flightRepo.save(flight);
        eventPublisher.publish(new FlightStatusChangedEvent(flight.getFlightId(), flight.getFlightNo(), prev, flight.getStatus(), reason));
        return flight;
    }

    @Override
    public DeviationReport getScheduleDeviationReport(String flightId) {
        Flight flight = getExistingFlight(flightId);
        return deviationAnalyzer.analyzeDeviation(flight);
    }

    private Flight getExistingFlight(String flightId) {
        return flightRepo.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Flight not found with ID: " + flightId));
    }
}
