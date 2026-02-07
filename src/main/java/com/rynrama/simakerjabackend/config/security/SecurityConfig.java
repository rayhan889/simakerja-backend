package com.rynrama.simakerjabackend.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/api/v1/**").authenticated();
                    auth.anyRequest().permitAll();
                })
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/login/oauth2/code/*")
                        )
                )
                // Disable CSRF for development purposes
                .csrf(csrf -> csrf.disable())
                .headers((headers) ->
                        headers
                                .contentSecurityPolicy(csp -> csp
                                        .policyDirectives(
                                                "default-src 'self'; " +
                                                        "script-src 'self'; " +
                                                        "style-src 'self' 'unsafe-inline'; " +
                                                        "img-src 'self' data: https:; " +
                                                        "connect-src 'self' http://localhost:3000; " +
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
