package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.config.security.CustomUserPrincipal;
import com.rynrama.simakerjabackend.dto.AuthDTO;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<GlobalAPIResponse<AuthDTO>> me(@AuthenticationPrincipal CustomUserPrincipal principal){
        AuthDTO authInfo = new AuthDTO();

//        Base User
        authInfo.setId(principal.getUser().getId());
        authInfo.setEmail(principal.getEmail());
        authInfo.setFullName(principal.getFullName());
        authInfo.setPhoneNumber(principal.getUser().getPhoneNumber());
        authInfo.setRole(principal.getUser().getRole());
        authInfo.setStatus(principal.getUser().getStatus());
        authInfo.setProfilePicture(principal.getPicture());

//        Student
        authInfo.setNim(principal.getNim());
        authInfo.setStudyProgram(principal.getStudyProgram());

//        Staff
        authInfo.setNip(principal.getNip());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GlobalAPIResponse.success(authInfo));
    }
}
