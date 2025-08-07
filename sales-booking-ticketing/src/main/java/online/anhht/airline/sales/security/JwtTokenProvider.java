package online.anhht.airline.sales.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long expirationSeconds;

    private static final Pattern SUB_PATTERN = Pattern.compile("\"sub\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern EXP_PATTERN = Pattern.compile("\"exp\"\\s*:\\s*(\\d+)");
    private static final Pattern ROLES_PATTERN = Pattern.compile("\"roles\"\\s*:\\s*\\[([^\\]]*)\\]");

    public JwtTokenProvider() {
        this("AirlineFlightsWorldwideSecretSecurityTokenKeyForJwtValidation2026", 86400L);
    }

    public JwtTokenProvider(
            @Value("${security.jwt.secret:AirlineFlightsWorldwideSecretSecurityTokenKeyForJwtValidation2026}") String secretKey,
            @Value("${security.jwt.expiration:86400}") long expirationSeconds
    ) {
        this.secretKey = secretKey;
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(String username, List<String> roles) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String headerEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(header.getBytes(StandardCharsets.UTF_8));

            StringBuilder rolesJson = new StringBuilder();
            if (roles != null) {
                for (int i = 0; i < roles.size(); i++) {
                    if (i > 0) rolesJson.append(",");
                    rolesJson.append("\"").append(roles.get(i)).append("\"");
                }
            }

            long iat = Instant.now().getEpochSecond();
            long exp = iat + expirationSeconds;
            String payload = String.format("{\"sub\":\"%s\",\"roles\":[%s],\"iat\":%d,\"exp\":%d}",
                    username, rolesJson, iat, exp);
            String payloadEncoded = Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            String dataToSign = headerEncoded + "." + payloadEncoded;
            String signature = sign(dataToSign);

            return dataToSign + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String dataToSign = parts[0] + "." + parts[1];
            String expectedSig = sign(dataToSign);
            if (!MessageDigest.isEqual(expectedSig.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher expMatcher = EXP_PATTERN.matcher(payload);
            if (expMatcher.find()) {
                long exp = Long.parseLong(expMatcher.group(1));
                return exp > Instant.now().getEpochSecond();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher subMatcher = SUB_PATTERN.matcher(payload);
            if (subMatcher.find()) {
                return subMatcher.group(1);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> extractRoles(String token) {
        try {
            String[] parts = token.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            Matcher rolesMatcher = ROLES_PATTERN.matcher(payload);
            List<String> roles = new ArrayList<>();
            if (rolesMatcher.find()) {
                String group = rolesMatcher.group(1).trim();
                if (!group.isEmpty()) {
                    for (String role : group.split(",")) {
                        String clean = role.replace("\"", "").trim();
                        if (!clean.isEmpty()) {
                            roles.add(clean);
                        }
                    }
                }
            }
            return roles;
        } catch (Exception e) {
            return List.of();
        }
    }

    private String sign(String data) throws Exception {
        Mac hmacSha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSha256.init(secretKeySpec);
        byte[] rawHmac = hmacSha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
    }
}
