package online.anhht.airline.operations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Flight Operations Microservice Integration Tests")
class FlightOperationsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/v1/operations/flights should return list of operational flights")
    void testGetAllFlights() throws Exception {
        mockMvc.perform(get("/api/v1/operations/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.flightId == 'FL-1001')]").exists());
    }

    @Test
    @DisplayName("State machine transitions: ON_TIME -> BOARDING -> DEPARTED -> ARRIVED")
    void testFlightLifecycleTransitions() throws Exception {
        // 1. Mark On Time
        mockMvc.perform(post("/api/v1/operations/flights/FL-1001/on-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ON_TIME"));

        // 2. Start Boarding
        mockMvc.perform(post("/api/v1/operations/flights/FL-1001/boarding"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOARDING"));

        // 3. Depart
        mockMvc.perform(post("/api/v1/operations/flights/FL-1001/depart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DEPARTED"));

        // 4. Arrive
        mockMvc.perform(post("/api/v1/operations/flights/FL-1001/arrive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARRIVED"));
    }

    @Test
    @DisplayName("GET /api/v1/operations/timetable should return synchronized UTC and local airport times")
    void testTimetableTimezones() throws Exception {
        mockMvc.perform(get("/api/v1/operations/timetable/FL-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightId").value("FL-1001"))
                .andExpect(jsonPath("$.scheduledDepartureUtc").exists())
                .andExpect(jsonPath("$.scheduledDepartureLocal").exists())
                .andExpect(jsonPath("$.departureTimezone").value("Europe/Moscow"));
    }

    @Test
    @DisplayName("GET /api/v1/operations/deviations/{flightId} should return delay analysis report")
    void testScheduleDeviationReport() throws Exception {
        mockMvc.perform(get("/api/v1/operations/deviations/FL-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightId").value("FL-1001"))
                .andExpect(jsonPath("$.delayCategory").exists());
    }
}
