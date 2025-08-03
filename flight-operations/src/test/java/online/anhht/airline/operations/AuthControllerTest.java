package online.anhht.airline.operations;

import online.anhht.airline.operations.adapter.inbound.AuthController;
import online.anhht.airline.operations.security.JwtTokenProvider;
import online.anhht.airline.operations.service.AuthService.AuthResponse;
import online.anhht.airline.operations.service.AuthService.LoginRequest;
import online.anhht.airline.operations.service.AuthService.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Flight Operations - Custom Authentication & OAuth2 Tests")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should successfully register customer and authenticate")
    void testCustomerRegistrationAndLogin() {
        String email = "ops_customer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest regReq = new RegisterRequest(
                email,
                "Password123!",
                "Operations Customer",
                "+12025550222"
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
    @DisplayName("Should login system pre-provisioned crew member")
    void testCrewLogin() {
        LoginRequest crewLogin = new LoginRequest("crew@airline.com", "CrewPassword123!");
        ResponseEntity<?> response = authController.login(crewLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse auth = (AuthResponse) response.getBody();
        assertEquals("ROLE_CREW", auth.role());
    }
}
