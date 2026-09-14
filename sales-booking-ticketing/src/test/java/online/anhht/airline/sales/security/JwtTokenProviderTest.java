package online.anhht.airline.sales.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
    }

    @Test
    void shouldGenerateAndValidateTokenSuccessfully() {
        String token = tokenProvider.generateToken("passenger_01", List.of("ROLE_PASSENGER"));
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("passenger_01", tokenProvider.extractUsername(token));
        List<String> roles = tokenProvider.extractRoles(token);
        assertEquals(1, roles.size());
        assertTrue(roles.contains("ROLE_PASSENGER"));
    }
}
