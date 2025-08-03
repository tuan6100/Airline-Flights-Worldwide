package online.anhht.airline.planning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Flight Planning & Scheduling Microservice Integration Tests")
class FlightPlanningIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/v1/airports should return all airports")
    void testGetAllAirports() throws Exception {
        mockMvc.perform(get("/api/v1/airports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.airportCode == 'SVO')]").exists());
    }

    @Test
    @DisplayName("GET /api/v1/airports/distance should calculate distance between two airports")
    void testCalculateAirportDistance() throws Exception {
        mockMvc.perform(get("/api/v1/airports/distance?from=SVO&to=LED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromAirport").value("SVO"))
                .andExpect(jsonPath("$.toAirport").value("LED"))
                .andExpect(jsonPath("$.distanceKm").isNumber());
    }

    @Test
    @DisplayName("GET /api/v1/fleet should return airline aircraft inventory")
    void testGetAllFleet() throws Exception {
        mockMvc.perform(get("/api/v1/fleet"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.id == '773-001')]").exists());
    }

    @Test
    @DisplayName("GET /api/v1/routes/find-path should find multi-leg path using graph traversal")
    void testFindRoutePath() throws Exception {
        mockMvc.perform(get("/api/v1/routes/find-path?departure=SVO&arrival=AER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.found").value(true))
                .andExpect(jsonPath("$.path").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/planning/routes/itinerary/shortest should return shortest itinerary via A* algorithm")
    void testGetShortestItinerary() throws Exception {
        mockMvc.perform(get("/api/v1/planning/routes/itinerary/shortest?from=SVO&to=AER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDirect").value(false))
                .andExpect(jsonPath("$.layoverCount").value(1))
                .andExpect(jsonPath("$.airportSequence[0]").value("SVO"))
                .andExpect(jsonPath("$.airportSequence[2]").value("AER"))
                .andExpect(jsonPath("$.totalDistanceKm").isNumber());
    }

    @Test
    @DisplayName("GET /api/v1/planning/routes/itinerary/alternatives should return alternative itineraries via Yen algorithm")
    void testGetAlternativeItineraries() throws Exception {
        mockMvc.perform(get("/api/v1/planning/routes/itinerary/alternatives?from=SVO&to=AER&k=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].airportSequence[0]").value("SVO"));
    }

    @Test
    @DisplayName("GET /api/v1/planning/routes/network/hubs should identify hub airports")
    void testGetHubAirports() throws Exception {
        mockMvc.perform(get("/api/v1/planning/routes/network/hubs?top=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/v1/planning/routes/network/reachability should return reachable destinations")
    void testGetReachability() throws Exception {
        mockMvc.perform(get("/api/v1/planning/routes/network/reachability?from=SVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origin").value("SVO"))
                .andExpect(jsonPath("$.reachableCount").isNumber())
                .andExpect(jsonPath("$.reachableAirports").isArray())
                .andExpect(jsonPath("$.shortestHops").isMap());
    }

    @Test
    @DisplayName("GET /api/v1/schedules/optimize should optimize flight schedule with Simulated Annealing or GA")
    void testOptimizeSchedule() throws Exception {
        mockMvc.perform(get("/api/v1/schedules/optimize?startDate=2026-06-01&endDate=2026-06-07&algorithm=SA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm").value("SA"))
                .andExpect(jsonPath("$.scheduledFlightCount").isNumber())
                .andExpect(jsonPath("$.assignments").isArray());
    }
}
