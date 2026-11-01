package com.weddinglage.seed;

import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.domain.AdminUser;
import com.weddinglage.repository.AdminUserRepository;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminUserSeeder {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final WeddinglageProperties properties;

    public AdminUserSeeder(
            AdminUserRepository adminUserRepository,
            PasswordEncoder passwordEncoder,
            WeddinglageProperties properties) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedAdmin() {
        String email = properties.getAdminEmail();
        adminUserRepository.findByEmailIgnoreCase(email).ifPresentOrElse(
                existing -> log.info("Admin user already present: {}", email),
                () -> {
                    AdminUser admin = new AdminUser();
                    admin.setEmail(email);
                    admin.setPasswordHash(passwordEncoder.encode(properties.getAdminPassword()));
                    admin.setRoles("ADMIN");
                    admin.setUpdatedAt(Instant.now());
                    adminUserRepository.save(admin);
                    log.info("Seeded admin user from env: {}", email);
                });
    }
}
