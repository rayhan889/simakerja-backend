package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.model.RefreshTokenModel;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.repository.RefreshTokenRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepo;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${jwt.refresh.expiration}") // 7 days
    private long refreshTokenExpiryMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, UserRepository userRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public String createRefreshToken(UserModel user) {
        String rawToken = generateSecureToken();

        RefreshTokenModel refreshToken = new RefreshTokenModel();
        refreshToken.setUser(user);
        refreshToken.setToken(rawToken);
        refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenExpiryMs));
        refreshToken.setCreatedAt(Instant.now());

        refreshTokenRepo.save(refreshToken);
        log.info("Refresh token created: {}", refreshToken);

        return rawToken;
    }

    @Transactional
    public RefreshResult validateAndRotate(String token) {
        log.debug("Validating refresh token: {}", token);
        RefreshTokenModel existing = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found. Possibly already rotated (token reused)");
                    return new IllegalArgumentException("Invalid refresh token");
                });

        if (existing.isExpired()) {
            refreshTokenRepo.delete(existing);
            log.warn("Expired refresh token for user: {}", existing.getUser().getId());
            throw new IllegalArgumentException("Refresh token is expired");
        }

        UserModel user = existing.getUser();

        refreshTokenRepo.delete(existing);

        String newToken = createRefreshToken(user);

        return new RefreshResult(newToken, user);
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepo.deleteByToken(token);
        log.info("Refresh token revoked");
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        UserModel user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        refreshTokenRepo.deleteAllByUser(user);
        log.info("Revoked all tokens for user: {}", user.getId());
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record RefreshResult(String token, UserModel user) {}
}
