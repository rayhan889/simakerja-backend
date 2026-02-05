package com.rynrama.simakerjabackend.controller;

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
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser oidcUser){
        return Map.of(
                "sub",oidcUser.getSubject(),
                "email", oidcUser.getEmail(),
                "name", oidcUser.getName(),
                "picture", oidcUser.getPicture()
        );
    }
}
