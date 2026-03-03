package com.rynrama.simakerjabackend.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthTokenEntryPoint unauthorizedHandler;

    @Autowired
    private AuthTokenFilter authTokenFilter;

    @Autowired
    private OauthSuccessHandler oauthSuccessHandler;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    // Public auth endpoints (cookie-based, no Bearer token)
                    auth.requestMatchers(
                            "/api/v1/auth/refresh",
                            "/api/v1/auth/logout",
                            "/api/v1/auth/login"
                    ).permitAll();
                    // All other /api/v1/** require authentication (Bearer token)
                    auth.requestMatchers("/api/v1/**").authenticated();
                    auth.anyRequest().permitAll();
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                        )
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/login/oauth2/code/*")
                        )
                        .successHandler(oauthSuccessHandler)
                )
                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .headers((headers) ->
                        headers
                                .contentSecurityPolicy(csp -> csp
                                        .policyDirectives(
                                                "default-src 'self'; " +
                                                        "script-src 'self'; " +
                                                        "style-src 'self' 'unsafe-inline'; " +
                                                        "img-src 'self' data: https:; " +
                                                        "connect-src 'self'; " +
                                                        "frame-ancestors 'none';"
                                        )
                                )
                                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                                .xssProtection(HeadersConfigurer.XXssConfig::disable)
                                .contentTypeOptions(Customizer.withDefaults())
                                .referrerPolicy(referrer -> referrer
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                                )
                )
                .cors(Customizer.withDefaults())
                .build();
    }
}
