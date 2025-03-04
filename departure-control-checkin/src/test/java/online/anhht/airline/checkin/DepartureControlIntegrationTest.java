package online.anhht.airline.checkin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("Departure Control & Check-In Microservice Integration Tests")
class DepartureControlIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("GET /api/v1/checkin/window should check 24-hour rule")
    void testCheckInWindowStatus() throws Exception {
        mockMvc.perform(get("/api/v1/checkin/window?flightId=FL-1001&hoursUntilDeparture=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCheckInOpen").value(true));

        mockMvc.perform(get("/api/v1/checkin/window?flightId=FL-1001&hoursUntilDeparture=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCheckInOpen").value(false));
    }

    @Test
    @DisplayName("POST /api/v1/checkin/segment should allocate seat and issue boarding pass")
    void testCheckInSegment() throws Exception {
        String jsonPayload = """
                {
                  "ticketNo": "0005432000001",
                  "passengerId": "PASSPORT-11111",
                  "passengerName": "ALEXEI SMIRNOV",
                  "flightId": "FL-1001",
                  "seatNo": "1A"
                }
                """;

        mockMvc.perform(post("/api/v1/checkin/segment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ticketNo").value("0005432000001"))
                .andExpect(jsonPath("$.flightId").value("FL-1001"))
                .andExpect(jsonPath("$.seatNo").value("1A"))
                .andExpect(jsonPath("$.boardingNo").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/checkin/through should perform through check-in for multiple connecting segments")
    void testThroughCheckIn() throws Exception {
        String jsonPayload = """
                {
                  "ticketNo": "0005432000002",
                  "passengerId": "PASSPORT-22222",
                  "passengerName": "TATIANA POPOVA",
                  "flight1Id": "FL-2001",
                  "seat1No": "1B",
                  "flight2Id": "FL-2002",
                  "seat2No": "2A"
                }
                """;

        mockMvc.perform(post("/api/v1/checkin/through")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].flightId").value("FL-2001"))
                .andExpect(jsonPath("$[0].seatNo").value("1B"))
                .andExpect(jsonPath("$[1].flightId").value("FL-2002"))
                .andExpect(jsonPath("$[1].seatNo").value("2A"));
    }
}
