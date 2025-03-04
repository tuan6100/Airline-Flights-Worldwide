package online.anhht.airline.inventory;

import online.anhht.airline.inventory.adapter.inbound.AuthController;
import online.anhht.airline.inventory.security.JwtTokenProvider;
import online.anhht.airline.inventory.service.AuthService.AuthResponse;
import online.anhht.airline.inventory.service.AuthService.LoginRequest;
import online.anhht.airline.inventory.service.AuthService.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Inventory - Custom Authentication & OAuth2 Tests")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should successfully register customer and authenticate")
    void testCustomerRegistrationAndLogin() {
        String email = "inv_customer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest regReq = new RegisterRequest(
                email,
                "Password123!",
                "Inventory Customer",
                "+12025550333"
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
    @DisplayName("Should login system pre-provisioned admin")
    void testAdminLogin() {
        LoginRequest adminLogin = new LoginRequest("admin@airline.com", "AdminPassword123!");
        ResponseEntity<?> response = authController.login(adminLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponse auth = (AuthResponse) response.getBody();
        assertEquals("ROLE_ADMIN", auth.role());
    }
}
