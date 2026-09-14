package online.anhht.airline.planning;

import online.anhht.airline.planning.adapter.inbound.AuthController;
import online.anhht.airline.planning.security.JwtTokenProvider;
import online.anhht.airline.planning.service.AuthService;
import online.anhht.airline.planning.service.AuthService.AuthResponse;
import online.anhht.airline.planning.service.AuthService.LoginRequest;
import online.anhht.airline.planning.service.AuthService.RegisterRequest;
import online.anhht.airline.planning.service.AuthService.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Flight Planning - Custom Authentication & OAuth2 Tests")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should successfully register customer and authenticate")
    void testCustomerRegistrationAndLogin() {
        String email = "planner_cust_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest regReq = new RegisterRequest(
                email,
                "Password123!",
                "Planning Customer",
                "+12025550111"
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

        Authentication auth = new UsernamePasswordAuthenticationToken(email.toLowerCase(), null, List.of());
        ResponseEntity<?> meResponse = authController.getCurrentUser(auth);
        assertEquals(HttpStatus.OK, meResponse.getStatusCode());
        UserProfileResponse profile = (UserProfileResponse) meResponse.getBody();
        assertEquals("Planning Customer", profile.fullName());
    }

    @Test
    @DisplayName("Should login system pre-provisioned admin")
    void testAdminLogin() {
        LoginRequest adminLogin = new LoginRequest("admin@airline.com", "AdminPassword123!");
        ResponseEntity<?> response = authController.login(adminLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse auth = (AuthResponse) response.getBody();
        assertEquals("ROLE_ADMIN", auth.role());
    }
}
