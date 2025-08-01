package online.anhht.airline.factory;

import online.anhht.airline.model.Airplane;
import online.anhht.airline.model.Airport;
import online.anhht.airline.model.BoardingPass;
import online.anhht.airline.model.Booking;
import online.anhht.airline.model.CabinLayout;
import online.anhht.airline.model.Coordinates;
import online.anhht.airline.model.FareCondition;
import online.anhht.airline.model.Flight;
import online.anhht.airline.model.Route;
import online.anhht.airline.model.Seat;
import online.anhht.airline.model.TemporalValidityRange;
import online.anhht.airline.model.Ticket;
import online.anhht.airline.model.TicketFlightSegment;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Default implementation of the AviationFactory.
 */
public enum DefaultAviationFactory implements AviationFactory {
    INSTANCE;

    @Override
    public Airport createAirport(String code, String name, String city, String country, Coordinates coords, ZoneId timezone, int capacity) {
        return new Airport(code, name, city, country, coords, timezone, capacity);
    }

    @Override
    public Airplane createAirplane(String id, String model, int rangeKm, int cruisingSpeedKmH, CabinLayout cabinLayout) {
        return new Airplane(id, model, rangeKm, cruisingSpeedKmH, cabinLayout);
    }

    @Override
    public CabinLayout createCabinLayout(String aircraftCode, List<Seat> seats) {
        return new CabinLayout(aircraftCode, seats);
    }

    @Override
    public Seat createSeat(String seatNo, FareCondition fareCondition, String aircraftCode) {
        return new Seat(seatNo, fareCondition, aircraftCode);
    }

    @Override
    public Route createRoute(String flightNo, Airport departure, Airport arrival, Set<DayOfWeek> days, LocalTime depTime, Duration duration, TemporalValidityRange validity) {
        return new Route(flightNo, departure, arrival, days, depTime, duration, validity);
    }

    @Override
    public Flight createFlight(String flightId, String flightNo, Route route, Airplane airplane, OffsetDateTime dep, OffsetDateTime arr) {
        return new Flight(flightId, flightNo, route, airplane, dep, arr);
    }

    @Override
    public Booking createBooking(String bookRef, OffsetDateTime bookDate) {
        return new Booking(bookRef, bookDate);
    }

    @Override
    public Ticket createTicket(String ticketNo, String bookRef, String passengerId, String passengerName, String contactData, List<TicketFlightSegment> segments) {
        return new Ticket(ticketNo, bookRef, passengerId, passengerName, contactData, segments);
    }

    @Override
    public TicketFlightSegment createSegment(String flightId, FareCondition fareCondition, BigDecimal price, boolean outbound) {
        return new TicketFlightSegment(flightId, fareCondition, price, outbound);
    }

    @Override
    public BoardingPass createBoardingPass(String ticketNo, String flightId, int boardingNo, String seatNo, OffsetDateTime boardingTime) {
        return new BoardingPass(ticketNo, flightId, boardingNo, seatNo, boardingTime);
    }
}
