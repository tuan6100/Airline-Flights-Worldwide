/**
 * Core domain module for Airline Flights Worldwide Management System.
 * <p>
 * Provides foundational domain entities, state machine transitions, domain events,
 * factory builders, graph routing/analytics algorithms, and specialized utilities.
 */
module online.anhht.airline.core {
    requires static lombok;
    requires static org.jspecify;

    exports online.anhht.airline.model;
    exports online.anhht.airline.factory;
    exports online.anhht.airline.events;
    exports online.anhht.airline.operations;
    exports online.anhht.airline.planning;
    exports online.anhht.airline.scheduling;
    exports online.anhht.airline.checkin;
    exports online.anhht.airline.util.collection;
    exports online.anhht.airline.util.graph;
}
