package online.anhht.airline.sales;

import online.anhht.airline.sales.adapter.inbound.AuthController;
import online.anhht.airline.sales.security.JwtTokenProvider;
import online.anhht.airline.sales.service.AuthService;
import online.anhht.airline.sales.service.AuthService.AuthResponse;
import online.anhht.airline.sales.service.AuthService.LoginRequest;
import online.anhht.airline.sales.service.AuthService.RegisterRequest;
import online.anhht.airline.sales.service.AuthService.UserProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Custom Authentication & OAuth2 Authorization Tests")
class AuthControllerTest {

    @Autowired
    private AuthController authController;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Should successfully register new customer and allow login")
    void testCustomerRegistrationAndLogin() {
        String email = "customer_" + System.currentTimeMillis() + "@example.com";
        RegisterRequest regReq = new RegisterRequest(
                email,
                "Password123!",
                "Alice Wonder",
                "+12025550199"
        );

        // 1. Register customer
        ResponseEntity<?> regResponse = authController.registerCustomer(regReq);
        assertEquals(HttpStatus.CREATED, regResponse.getStatusCode());
        assertNotNull(regResponse.getBody());
        assertTrue(regResponse.getBody() instanceof AuthResponse);

        AuthResponse regAuth = (AuthResponse) regResponse.getBody();
        assertEquals(email.toLowerCase(), regAuth.email());
        assertEquals("ROLE_PASSENGER", regAuth.role());
        assertNotNull(regAuth.accessToken());
        assertTrue(jwtTokenProvider.validateToken(regAuth.accessToken()));

        // 2. Duplicate registration with same email should be rejected
        ResponseEntity<?> duplicateResponse = authController.registerCustomer(regReq);
        assertEquals(HttpStatus.BAD_REQUEST, duplicateResponse.getStatusCode());

        // 3. Login with registered customer
        LoginRequest loginReq = new LoginRequest(email, "Password123!");
        ResponseEntity<?> loginResponse = authController.login(loginReq);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        AuthResponse loginAuth = (AuthResponse) loginResponse.getBody();
        assertNotNull(loginAuth);
        assertEquals(email.toLowerCase(), loginAuth.email());
        assertEquals("ROLE_PASSENGER", loginAuth.role());

        // 4. Verify JWT claims
        List<String> roles = jwtTokenProvider.extractRoles(loginAuth.accessToken());
        assertTrue(roles.contains("ROLE_PASSENGER"));
        assertEquals(email.toLowerCase(), jwtTokenProvider.extractUsername(loginAuth.accessToken()));

        // 5. Test get profile with Authentication context
        Authentication auth = new UsernamePasswordAuthenticationToken(email.toLowerCase(), null, List.of());
        ResponseEntity<?> meResponse = authController.getCurrentUser(auth);
        assertEquals(HttpStatus.OK, meResponse.getStatusCode());
        UserProfileResponse profile = (UserProfileResponse) meResponse.getBody();
        assertNotNull(profile);
        assertEquals(email.toLowerCase(), profile.email());
        assertEquals("Alice Wonder", profile.fullName());
        assertEquals("ROLE_PASSENGER", profile.role());
    }

    @Test
    @DisplayName("Should authenticate system pre-provisioned admin account")
    void testSystemAdminLogin() {
        LoginRequest adminLogin = new LoginRequest("admin@airline.com", "AdminPassword123!");
        ResponseEntity<?> response = authController.login(adminLogin);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AuthResponse auth = (AuthResponse) response.getBody();
        assertNotNull(auth);
        assertEquals("admin@airline.com", auth.email());
        assertEquals("ROLE_ADMIN", auth.role());
        assertTrue(jwtTokenProvider.validateToken(auth.accessToken()));
        assertTrue(jwtTokenProvider.extractRoles(auth.accessToken()).contains("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("Should authenticate system pre-provisioned crew and ground staff accounts")
    void testCrewAndGroundStaffLogin() {
        LoginRequest crewLogin = new LoginRequest("crew@airline.com", "CrewPassword123!");
        ResponseEntity<?> crewResp = authController.login(crewLogin);
        assertEquals(HttpStatus.OK, crewResp.getStatusCode());
        assertEquals("ROLE_CREW", ((AuthResponse) crewResp.getBody()).role());

        LoginRequest groundLogin = new LoginRequest("groundstaff@airline.com", "GroundStaff123!");
        ResponseEntity<?> groundResp = authController.login(groundLogin);
        assertEquals(HttpStatus.OK, groundResp.getStatusCode());
        assertEquals("ROLE_GROUND_STAFF", ((AuthResponse) groundResp.getBody()).role());
    }

    @Test
    @DisplayName("Should reject login with invalid password")
    void testLoginWithWrongPassword() {
        LoginRequest wrongLogin = new LoginRequest("admin@airline.com", "WrongPassword123!");
        ResponseEntity<?> response = authController.login(wrongLogin);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals("Invalid email or password", body.get("error"));
    }
}
