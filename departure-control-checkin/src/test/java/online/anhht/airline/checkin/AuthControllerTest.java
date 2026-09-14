package online.anhht.airline.checkin;

import online.anhht.airline.checkin.adapter.inbound.AuthController;
import online.anhht.airline.checkin.security.JwtTokenProvider;
import online.anhht.airline.checkin.service.AuthService.AuthResponse;
import online.anhht.airline.checkin.service.AuthService.LoginRequest;
import online.anhht.airline.checkin.service.AuthService.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Departure Control & Check-in - Custom Authentication & OAuth2 Tests")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should successfully register customer and authenticate")
    void testCustomerRegistrationAndLogin() {
        String email = "drc_customer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest regReq = new RegisterRequest(
                email,
                "Password123!",
                "Checkin Customer",
                "+12025550444"
        );

        ResponseEntity<?> regResponse = authController.registerCustomer(regReq);
        assertEquals(HttpStatus.CREATED, regResponse.getStatusCode());
        AuthResponse regAuth = (AuthResponse) regResponse.getBody();
        assertNotNull(regAuth);
        assertEquals(email.toLowerCase(), regAuth.email());
        assertEquals("ROLE_PASSENGER", regAuth.role());

        LoginRequest loginReq = new LoginRequest(email, "Password123!");
        ResponseEntity<?> loginResponse = authController.login(loginReq);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse loginAuth = (AuthResponse) loginResponse.getBody();
        assertNotNull(loginAuth);
        assertTrue(jwtTokenProvider.validateToken(loginAuth.accessToken()));
    }

    @Test
    @DisplayName("Should login system pre-provisioned ground staff")
    void testGroundStaffLogin() {
        LoginRequest groundLogin = new LoginRequest("groundstaff@airline.com", "GroundStaff123!");
        ResponseEntity<?> response = authController.login(groundLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse auth = (AuthResponse) response.getBody();
        assertEquals("ROLE_GROUND_STAFF", auth.role());
    }
}
