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
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

/**
 * Central domain factory for constructing aviation core domain entities.
 */
public interface AviationFactory {

    Airport createAirport(String code, String name, String city, String country, Coordinates coords, ZoneId timezone, int capacity);

    Airplane createAirplane(String id, String model, int rangeKm, int cruisingSpeedKmH, CabinLayout cabinLayout);

    CabinLayout createCabinLayout(String aircraftCode, List<Seat> seats);

    Seat createSeat(String seatNo, FareCondition fareCondition, String aircraftCode);

    Route createRoute(String flightNo, Airport departure, Airport arrival, Set<DayOfWeek> days, LocalTime depTime, Duration duration, TemporalValidityRange validity);

    Flight createFlight(String flightId, String flightNo, Route route, Airplane airplane, OffsetDateTime dep, OffsetDateTime arr);

    Booking createBooking(String bookRef, OffsetDateTime bookDate);

    Ticket createTicket(String ticketNo, String bookRef, String passengerId, String passengerName, String contactData, List<TicketFlightSegment> segments);

    TicketFlightSegment createSegment(String flightId, FareCondition fareCondition, BigDecimal price, boolean outbound);

    BoardingPass createBoardingPass(String ticketNo, String flightId, int boardingNo, String seatNo, OffsetDateTime boardingTime);

    static AviationFactory getDefault() {
        return DefaultAviationFactory.INSTANCE;
    }
}
