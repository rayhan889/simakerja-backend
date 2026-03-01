package com.rynrama.simakerjabackend.config.security;

import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.service.RefreshTokenService;
import com.rynrama.simakerjabackend.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OauthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${jwt.refresh.expiration}") // 7 days
    private long refreshTokenExpiryMs;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    public OauthSuccessHandler(RefreshTokenService refreshTokenService, UserRepository userRepository) {
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.debug("OauthSuccessHandler triggered");

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        if  (principal == null) {
            log.warn("Authentication failed: no principal found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
            return;
        }

        UserModel user = principal.getUser();
        String picture = principal.getAttributes().get("picture") != null
                ? principal.getAttributes().get("picture").toString()
                : null;

        if (picture != null) {
            user.setProfilePictureUrl(picture);
            userRepository.save(user);
        }
        log.debug("Authenticated user: id={}, email={}", user.getId(), user.getEmail());

        String refreshToken = refreshTokenService.createRefreshToken(user);
        log.debug("Refresh token created for user {}", user.getId());

        int maxAgeSeconds = (int) (refreshTokenExpiryMs / 1000);
        CookieUtil.addCookie(
                response,
                "refresh_token",
                refreshToken,
                maxAgeSeconds,
                true,           // httpOnly — JS cannot read this
                cookieSecure,   // secure — true in production (HTTPS only)
                "/api/v1/auth"  // path — only sent for auth endpoints
        );
        log.debug("Set-Cookie header added: name=refresh_token, maxAge={}s, secure={}, path=/api/v1/auth",
                maxAgeSeconds, cookieSecure);

        CookieUtil.deleteCookie(
                response,
                HttpCookieOAuth2AuthorizationRequestRepository.OAUTH2_AUTH_REQUEST_COOKIE,
                "/"
        );

        log.info("OAuth2 login success for user {}. Redirecting to frontend: {}", user.getId(), frontendUrl);

        getRedirectStrategy().sendRedirect(request, response, frontendUrl);
    }
}
