package online.anhht.airline.operations.service;

import online.anhht.airline.operations.entity.UserEntity;
import online.anhht.airline.operations.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class SystemUserInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemUserInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.seed.admin.email:admin@airline.com}")
    private String adminEmail;

    @Value("${security.seed.admin.password:AdminPassword123!}")
    private String adminPassword;

    @Value("${security.seed.admin.name:System Administrator}")
    private String adminName;

    @Value("${security.seed.admin.phone:+12025550101}")
    private String adminPhone;

    @Value("${security.seed.crew.email:crew@airline.com}")
    private String crewEmail;

    @Value("${security.seed.crew.password:CrewPassword123!}")
    private String crewPassword;

    @Value("${security.seed.crew.name:Senior Captain Smith}")
    private String crewName;

    @Value("${security.seed.crew.phone:+12025550102}")
    private String crewPhone;

    @Value("${security.seed.groundstaff.email:groundstaff@airline.com}")
    private String groundStaffEmail;

    @Value("${security.seed.groundstaff.password:GroundStaff123!}")
    private String groundStaffPassword;

    @Value("${security.seed.groundstaff.name:Gate Agent Elena}")
    private String groundStaffName;

    @Value("${security.seed.groundstaff.phone:+12025550103}")
    private String groundStaffPhone;

    @Value("${security.seed.customer.email:customer@airline.com}")
    private String customerEmail;

    @Value("${security.seed.customer.password:CustomerPassword123!}")
    private String customerPassword;

    @Value("${security.seed.customer.name:John Doe}")
    private String customerName;

    @Value("${security.seed.customer.phone:+12025550104}")
    private String customerPhone;

    public SystemUserInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUserIfNotExists(adminEmail, adminPassword, adminName, adminPhone, "ROLE_ADMIN");
        seedUserIfNotExists(crewEmail, crewPassword, crewName, crewPhone, "ROLE_CREW");
        seedUserIfNotExists(groundStaffEmail, groundStaffPassword, groundStaffName, groundStaffPhone, "ROLE_GROUND_STAFF");
        seedUserIfNotExists(customerEmail, customerPassword, customerName, customerPhone, "ROLE_PASSENGER");
    }

    private void seedUserIfNotExists(String email, String rawPassword, String fullName, String phone, String role) {
        String normalizedEmail = email.toLowerCase().trim();
        if (!userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            UserEntity user = UserEntity.builder()
                    .id(UUID.randomUUID().toString())
                    .email(normalizedEmail)
                    .password(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .phoneNumber(phone)
                    .role(role)
                    .enabled(true)
                    .createdAt(OffsetDateTime.now())
                    .build();
            userRepository.save(user);
            log.info("Provisioned system user: {} with role: {}", normalizedEmail, role);
        }
    }
}
