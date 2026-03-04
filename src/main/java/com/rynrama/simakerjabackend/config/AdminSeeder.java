package com.rynrama.simakerjabackend.config;

import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.model.UserRole;
import com.rynrama.simakerjabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String @NonNull ... args) {
        log.info("AdminSeeder: starting..");
        String email = System.getenv("ADMIN_SEED_EMAIL");
        String password = System.getenv("ADMIN_SEED_PASSWORD");
        String fullName = System.getenv("ADMIN_SEED_FULL_NAME");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("AdminSeeder: ADMIN_SEED_EMAIL or ADMIN_SEED_PASSWORD env vars is null or blank. Skipping...");
            return;
        }

        if (password.length() < 8) {
            log.warn("AdminSeeder: ADMIN_SEED_PASSWORD length should be at least 8 characters. Skipping...");
            return;
        }

        String promoteToSuperadmin = System.getenv("ADMIN_SEED_PROMOTE_TO_SUPERADMIN");

        var role = UserRole.staff;

        if ("true".equalsIgnoreCase(promoteToSuperadmin)) {
            role = UserRole.superadmin;
            log.info("AdminSeeder: Promoting user {} to superadmin", email);
        }

        if (userRepo.findByEmail(email).isPresent()) {
            log.warn("AdminSeeder: User with email {} already exists. Skipping...", email);
            return;
        }

        var user = new UserModel();

        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(role);
        user.setCreatedAt(Instant.now());
        user.setPasswordHash(passwordEncoder.encode(password));

        userRepo.save(user);

        log.info("AdminSeeder: password set for user email={}, role={}", email, user.getRole());
    }
}
