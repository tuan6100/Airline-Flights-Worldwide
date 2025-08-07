package online.anhht.airline.sales;

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
@DisplayName("Sales, Booking & Ticketing Microservice Integration Tests")
class SalesBookingIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("POST /api/v1/sales/bookings should create group booking with dynamic passenger identification and return total")
    void testCreateGroupBooking() throws Exception {
        String jsonPayload = """
                {
                  "passengers": [
                    {
                      "passengerId": "PASSPORT-12345",
                      "passengerName": "IVAN PETROV",
                      "contactData": "{\\"email\\":\\"ivan@example.com\\"}",
                      "segments": [
                        {
                          "flightId": "FL-1001",
                          "fareCondition": "ECONOMY",
                          "price": 120.00,
                          "outbound": true
                        },
                        {
                          "flightId": "FL-1002",
                          "fareCondition": "ECONOMY",
                          "price": 150.00,
                          "outbound": false
                        }
                      ]
                    },
                    {
                      "passengerId": "PASSPORT-67890",
                      "passengerName": "OLGA PETROVA",
                      "contactData": "{\\"email\\":\\"olga@example.com\\"}",
                      "segments": [
                        {
                          "flightId": "FL-1001",
                          "fareCondition": "ECONOMY",
                          "price": 120.00,
                          "outbound": true
                        },
                        {
                          "flightId": "FL-1002",
                          "fareCondition": "ECONOMY",
                          "price": 150.00,
                          "outbound": false
                        }
                      ]
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/sales/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookRef").exists())
                .andExpect(jsonPath("$.passengerCount").value(2))
                .andExpect(jsonPath("$.totalAmount").value(540.00))
                .andExpect(jsonPath("$.tickets[0].passengerName").value("IVAN PETROV"))
                .andExpect(jsonPath("$.tickets[0].roundTrip").value(true))
                .andExpect(jsonPath("$.tickets[1].passengerName").value("OLGA PETROVA"));
    }
}
