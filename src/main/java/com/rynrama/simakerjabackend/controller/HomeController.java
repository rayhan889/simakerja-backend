package com.rynrama.simakerjabackend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "Hello, home!";
    }

    @GetMapping("/secured")
    public String secured(){
        return "Hello, secured!";
    }

    @GetMapping("/user")
    public Map<String, Object> authInfo(Authentication authentication){
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        Map<String, Object> result = new HashMap<>();
        result.put("sub", oidcUser.getSubject());
        result.put("email", oidcUser.getEmail());
        result.put("name", oidcUser.getFullName());
        result.put("claims", oidcUser.getClaims());

        return result;
    }
}
