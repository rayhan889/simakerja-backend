package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@Slf4j
public class LoginAttemptService {

    @Value("${app.max.failed.attempts}")
    private int maxFailedAttempts;

    @Value("${app.lockout.duration.minutes}")
    private int lockoutDurationMinutes;

    private final UserRepository userRepo;

    public LoginAttemptService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailedAttempt(UserModel user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= maxFailedAttempts) {
            Instant lockUntil = Instant.now().plus(lockoutDurationMinutes, ChronoUnit.MINUTES);
            user.setLockedUntil(lockUntil);
            log.warn("Account locked: email={}, until={}", user.getEmail(), lockUntil);
        }

        log.debug("saving user info userId={}. failed attempts={}",  user.getId(), attempts);
        userRepo.save(user);
    }
}
