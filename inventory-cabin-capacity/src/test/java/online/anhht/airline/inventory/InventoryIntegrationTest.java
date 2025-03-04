package online.anhht.airline.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Inventory & Cabin Capacity Microservice Integration Tests")
class InventoryIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/v1/inventory/layouts should return all aircraft cabin layouts")
    void testGetAllLayouts() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/layouts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.aircraftCode == '773')]").exists())
                .andExpect(jsonPath("$[?(@.aircraftCode == '320')]").exists());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/capacity/{aircraftCode} should return segmented capacities")
    void testGetCapacityBreakdown() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/capacity/773"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aircraftCode").value("773"))
                .andExpect(jsonPath("$.totalCapacity").isNumber())
                .andExpect(jsonPath("$.capacityByTravelClass.BUSINESS").isNumber())
                .andExpect(jsonPath("$.capacityByTravelClass.COMFORT").isNumber())
                .andExpect(jsonPath("$.capacityByTravelClass.ECONOMY").isNumber());
    }

    @Test
    @DisplayName("GET /api/v1/inventory/layouts/{aircraftCode}/seats/validate should check seat existence")
    void testValidateSeat() throws Exception {
        mockMvc.perform(get("/api/v1/inventory/layouts/773/seats/validate?seatNo=1A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValidSeat").value(true));

        mockMvc.perform(get("/api/v1/inventory/layouts/773/seats/validate?seatNo=99Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isValidSeat").value(false));
    }
}
