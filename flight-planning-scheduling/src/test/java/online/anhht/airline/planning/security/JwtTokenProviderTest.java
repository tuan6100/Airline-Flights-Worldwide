package online.anhht.airline.planning.security;

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
        String token = tokenProvider.generateToken("admin_user", List.of("ROLE_ADMIN", "ROLE_CREW"));
        assertNotNull(token);
        assertTrue(tokenProvider.validateToken(token));
        assertEquals("admin_user", tokenProvider.extractUsername(token));
        List<String> roles = tokenProvider.extractRoles(token);
        assertEquals(2, roles.size());
        assertTrue(roles.contains("ROLE_ADMIN"));
        assertTrue(roles.contains("ROLE_CREW"));
    }

    @Test
    void shouldRejectTamperedToken() {
        String token = tokenProvider.generateToken("user1", List.of("ROLE_PASSENGER"));
        String tampered = token + "tampered";
        assertFalse(tokenProvider.validateToken(tampered));
    }
}
