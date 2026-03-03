package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.dto.AuthDTO;
import com.rynrama.simakerjabackend.model.StaffModel;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.model.UserRole;
import com.rynrama.simakerjabackend.repository.StaffRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION_MINUTES = 15;

    private final UserRepository userRepo;
    private final StaffRepository staffRepo;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    public AuthService(
            UserRepository userRepo,
            StaffRepository staffRepo,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            JwtUtil jwtUtil
    ) {
        this.userRepo = userRepo;
        this.staffRepo = staffRepo;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    public record LoginResult(
      String accessToken,
      String refreshToken,
      AuthDTO user
    ){}

    @Transactional
    public LoginResult login(String email, String password) {
        UserModel user = userRepo.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", email);
                    return new IllegalArgumentException("User with email " + email + " not found");
                });

        if (!Objects.equals(user.getStatus(), "active")) {
            log.warn("User with email {} has been disabled", email);
            throw new DisabledException("User with email " + email + " is disabled");
        }

        if (user.getRole() != UserRole.staff && user.getRole() != UserRole.superadmin) {
            log.warn("Backdoor auth rejected. email={} and role={} no allowed.",  email, user.getRole());
            throw new ForbiddenLoginException("Password-based login is not available for this account");
        }

        if (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {
            log.warn("Backdoor auth rejected. email={} has no password configured", email);
            throw new ForbiddenLoginException("Password not configured for this account");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            log.warn("Backdoor auth rejected. email={} gets locked until={}", email, user.getLockedUntil() );
            throw new AccountLockedException("Account locked. Try again after " + user.getLockedUntil());
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
//            handle failed attempt
            handleAttemptFailed(user);
            log.warn("Backdoor auth failed. Remaining attempts={}",  user.getFailedLoginAttempts());
            throw new IllegalArgumentException("Password-based login failed. Remaining attempts=" + user.getFailedLoginAttempts());
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepo.save(user);

        log.info("Backdoor auth success. email={}, userId={}, role={}", user.getEmail(), user.getId(), user.getRole());

        String refreshToken = refreshTokenService.createRefreshToken(user);

        StaffModel staff = staffRepo.findByUserId(user.getId())
                .orElse(null);
        String accessToken = jwtUtil.generateToken(user, null, staff, user.getProfilePictureUrl());

        AuthDTO dto = buildAuthDTO(user, staff);

        return new LoginResult(accessToken, refreshToken, dto);
    }

    public void handleAttemptFailed(UserModel user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts > MAX_FAILED_ATTEMPTS) {
            Instant lockUntil = Instant.now().plus(LOCKOUT_DURATION_MINUTES, ChronoUnit.MINUTES);
            user.setLockedUntil(lockUntil);
            log.warn("Account locked: email={}, until={}", user.getEmail(), lockUntil);
        }

        userRepo.save(user);
    }

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        UserModel user = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", userId);
                    return new  IllegalArgumentException("User with id " + userId + " not found");
                });

        if (
                (user.getPasswordHash() == null || user.getPasswordHash().isBlank()) ||
                        !passwordEncoder.matches(newPassword, user.getPasswordHash())
        ) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        refreshTokenService.revokeAllForUser(userId);

        log.info("Password has changed for userId={}, all related refresh tokens revoked", userId);
    }

    private AuthDTO buildAuthDTO(UserModel user, StaffModel staff) {
        AuthDTO dto = new AuthDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        if (user.getProfilePictureUrl() != null) {
            dto.setProfilePicture(user.getProfilePictureUrl());
        }

        if (staff != null) {
            dto.setNip(staff.getNip());
        }

        return dto;
    }

    public static class ForbiddenLoginException extends RuntimeException {
        public ForbiddenLoginException(String message) {
            super(message);
        }
    }

    public static class AccountLockedException extends RuntimeException {
        public AccountLockedException(String message) {
            super(message);
        }
    }

}
