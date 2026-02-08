package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.config.security.CustomUserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal CustomUserPrincipal principal){
        return Map.of(
                "sub",principal.getSubject(),
                "email", principal.getEmail(),
                "name", principal.getName(),
                "picture", principal.getPicture(),
                "role", principal.getUser().getRole().name()
        );
    }
}
