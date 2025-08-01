# Airline Flights Worldwide Management System

## Key concepts

*   Research on query optimization solutions for large database: Develop the business domain based on the database provided on the
website https://postgrespro.com/community/demodb. Test and measure the performance of queries—such as flight searches and
schedule lookups—by partitioning data according to quarterly periods. Address the issue of concurrent booking by moving frequently
updated fields to a separate table, thereby avoiding row-level locking on related records.

*   Object-oriented design: Model the domain object based on state transitions and behaviors using the State pattern. Implement various
flight scheduling algorithms, such as Simulated Annealing Algorithm and Genetic Algorithm using the Strategy pattern, and configure the
specific algorithm employed via Dependency Injection.

## Business Domains

1. Flight Planning & Network Scheduling
This domain acts as the strategic "brain," establishing the foundational infrastructure—including airports, the fleet, and theoretical flight routes—before any ticket sales take place.
* Airport Network Infrastructure Management (airports_data, airports): Defines nodes within the global flight network using IATA airport codes (3-letter), geographic coordinates, local time zones, and countries.
* Fleet Capability Management (airplanes_data, airplanes): Manages the inventory of owned aircraft types, maximum range, and cruising speed to determine which aircraft are technically qualified to operate specific routes.
* Strategic Route Setup (routes): Plans non-stop flight paths connecting origins and destinations, including weekly operating frequency (days_of_week), estimated flight duration, and fixed weekly departure times.
* Route Lifecycle Management (Temporal Lifecycle): The route map is updated on a monthly cycle (managed via a validity time range in `tstzrange` format). This allows the airline to flexibly adjust its flight network based on seasonality or to optimize commercial performance.

2. Flight Operations
This domain is responsible for converting theoretical flight plans from the planning domain into actual flights based on the calendar date and monitoring their operational status in real-time.
* Flight realization: Automatically generates specific flights based on route schedules exactly 60 days prior to the flight date. 
* Flight Status State Machine management: Controls the flight lifecycle through strict operational states: Scheduled $\rightarrow$ On Time / Delayed $\rightarrow$ Boarding $\rightarrow$ Departed $\rightarrow$ Arrived or Cancelled. 
* Schedule deviation monitoring: Compares scheduled takeoff/landing times with actual times to track on-time performance and analyze reasons for delays. 
* Global time zone synchronization (timetable view): Simultaneously converts departure/arrival times between international standard UTC and local airport times to facilitate seamless coordination among flight crews, ground staff, and passengers.

3. Inventory & Cabin Capacity
Ensures the optimization of aircraft seating layouts and the management of service classes offered on each flight.
* Cabin Layout Design (Seats): Defines the fixed physical seating configuration for each aircraft model. The system assumes a unique cabin configuration for each aircraft type.
* Service Class Segmentation (Travel Class Management): Divides aircraft capacity into distinct service classes (fare conditions)—Economy, Comfort (Premium Economy), and Business—to support various pricing strategies.

4. Sales, Booking & Ticketing
This commercial business domain generates direct revenue for the airline by processing customer ticket purchases.
* Group Booking Management: Enables a single user to make a booking for themselves and a group of accompanying travelers within a single transaction (book_ref), while recording the total payment amount (total_amount) and the transaction timestamp.
* Dynamic Passenger Identification (Tickets): Instead of relying on a static customer profile (CRM), the system dynamically identifies passengers directly on each ticket using their identification document number (passenger_id) and full name (passenger_name). This reflects standard international aviation practices, where passenger information is inextricably linked to the legal travel documents used for the flight.
* Complex Multi-leg Itinerary Construction (Segments): Automatically splits a customer's ticket into multiple connecting flight segments when a direct flight is unavailable, while applying specific fare conditions (fare_conditions) and pricing (price) to each individual segment.
* Round-trip Segment Classification (Outbound Boolean): Distinguishes between outbound and return flight segments to manage policies regarding ticket changes, cancellations, or the application of special round-trip fares.

5. Departure Control & Check-In Domain
This domain operates directly at the airport, linking ticketed passengers to the specific flight preparing for departure.
* Check-In Window: Automatically activates the check-in process 24 hours prior to the scheduled departure time.
* Through Check-In: Upon the initial check-in at the departure airport, passengers are issued boarding passes for all subsequent flight segments included in their ticket.
* Seat Allocation & No-Double-Booking: Issues boarding passes specifying the seat number and boarding time. The system applies a UNIQUE constraint to the foreign key pair (flight_id, seat_no) to ensure that two boarding passes are never issued for the same seat.
* Boarding Control: Assigns a unique boarding sequence number (boarding_no) to each passenger for a specific flight, enabling ground staff to effectively manage the flow of passengers boarding the aircraft.

### End-to-end interaction across business domains:
A complete aviation business lifecycle operates as follows:
* Domain 1 plans theoretical flight routes.
* Domain 2 uses this information to activate and create actual flights 60 days in advance.
* Domain 4 opens ticket sales based on capacity configurations provided by Domain 3.
* Upon reaching the flight date (24 hours prior), Domain 5 opens the check-in process, allowing passengers to select seats and receive boarding passes, followed by the flight operation managed directly by Domain 2.


