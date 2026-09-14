package online.anhht.airline.operations.service;

import online.anhht.airline.operations.entity.UserEntity;
import online.anhht.airline.operations.repository.UserRepository;
import online.anhht.airline.operations.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public record RegisterRequest(
            String email,
            String password,
            String fullName,
            String phoneNumber
    ) {}

    public record LoginRequest(
            String email,
            String password
    ) {}

    public record AuthResponse(
            String accessToken,
            String tokenType,
            long expiresIn,
            String email,
            String fullName,
            String role
    ) {}

    public record UserProfileResponse(
            String id,
            String email,
            String fullName,
            String phoneNumber,
            String role,
            boolean enabled,
            String createdAt
    ) {}

    @Transactional
    public AuthResponse registerCustomer(RegisterRequest request) {
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.password() == null || request.password().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("User with email '" + normalizedEmail + "' already exists");
        }

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID().toString())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .phoneNumber(request.phoneNumber())
                .role("ROLE_PASSENGER")
                .enabled(true)
                .createdAt(OffsetDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail(), List.of(user.getRole()));
        return new AuthResponse(
                token,
                "Bearer",
                86400,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("Email and password are required");
        }

        String normalizedEmail = request.email().trim().toLowerCase();
        UserEntity user = userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isEnabled()) {
            throw new IllegalStateException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), List.of(user.getRole()));
        return new AuthResponse(
                token,
                "Bearer",
                86400,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String email) {
        UserEntity user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getRole(),
                user.isEnabled(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : null
        );
    }
}
