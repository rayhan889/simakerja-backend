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

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            log.warn("AdminSeeder: ADMIN_SEED_EMAIL or ADMIN_SEED_PASSWORD env vars is null or blank. Skipping...");
            return;
        }

        if (password.length() < 8) {
            log.warn("AdminSeeder: ADMIN_SEED_PASSWORD length should be at least 8 characters. Skipping...");
            return;
        }

        UserModel user = userRepo.findByEmail(email).orElse(null);

        if (user == null) {
            log.warn("AdminSeeder: user with email={} not found. Skipping...", email);
            return;
        }

        user.setPasswordHash(passwordEncoder.encode(password));

        String promoteToSuperadmin = System.getenv("ADMIN_SEED_PROMOTE_TO_SUPERADMIN");
        if ("true".equalsIgnoreCase(promoteToSuperadmin)) {
            user.setRole(UserRole.superadmin);
            log.info("AdminSeeder: Promoting user {} to superadmin", email);
        }

        userRepo.save(user);
        log.info("AdminSeeder: password set for user email={}, role={}", email, user.getRole());
    }
}
