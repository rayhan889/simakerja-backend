package com.rynrama.simakerjabackend.controller;

import com.rynrama.simakerjabackend.config.security.CustomUserPrincipal;
import com.rynrama.simakerjabackend.dto.AuthDTO;
import com.rynrama.simakerjabackend.model.StaffModel;
import com.rynrama.simakerjabackend.model.StudentModel;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.repository.StaffRepository;
import com.rynrama.simakerjabackend.repository.StudentRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import com.rynrama.simakerjabackend.service.RefreshTokenService;
import com.rynrama.simakerjabackend.util.CookieUtil;
import com.rynrama.simakerjabackend.util.GlobalAPIResponse;
import com.rynrama.simakerjabackend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepo;
    private final StudentRepository  studentRepo;
    private final StaffRepository staffRepo;
    private final JwtUtil  jwtUtil;

    @Value("${app.cookie.secure}") // def: false
    private boolean cookieSecure;

    @Value("${jwt.refresh.expiration}") // def: 7 days
    private long refreshTokenExpiryInMs;

    public AuthController(
            RefreshTokenService refreshTokenService,
            UserRepository userRepo,
            StudentRepository studentRepo,
            StaffRepository staffRepo,
            JwtUtil jwtUtil)
    {
        this.refreshTokenService = refreshTokenService;
        this.userRepo = userRepo;
        this.studentRepo = studentRepo;
        this.staffRepo = staffRepo;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/refresh")
    public ResponseEntity<GlobalAPIResponse<Map<String, Object>>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Cookie cookie = CookieUtil.getCookie(request, "refresh_token").orElse(null);

        if (cookie == null || cookie.getValue().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalAPIResponse.error("No refresh token provided"));
        }

        try {
            RefreshTokenService.RefreshResult result =
                    refreshTokenService.validateAndRotate(cookie.getValue());

            UserModel user = result.user();
            String newRefreshToken = result.token();

            int maxAgeSeconds = (int) refreshTokenExpiryInMs / 1000;
            CookieUtil.addCookie(
                    response,
                    "refresh_token",
                    newRefreshToken,
                    maxAgeSeconds,
                    true,
                    cookieSecure,
                    "/api/v1/auth"
            );

            StudentModel student = null;
            StaffModel staff = null;

            switch (user.getRole()) {
                case student -> student = studentRepo.findByUserId(user.getId()).orElse(null);
                case staff -> staff = staffRepo.findByUserId(user.getId()).orElse(null);
            }

            String accessToken = jwtUtil.generateToken(user, student, staff, user.getProfilePictureUrl());

            Map<String, Object> data = new HashMap<>();
            data.put("accessToken", accessToken);
            data.put("user", buildAuthDTO(user, student, staff));

            return ResponseEntity.ok(GlobalAPIResponse.success(data));
        } catch (IllegalArgumentException e) {
            CookieUtil.deleteCookie(response, "refresh_token", "/api/v1/auth");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(GlobalAPIResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<GlobalAPIResponse<AuthDTO>> me(){
        String userId = (String) Objects.requireNonNull(SecurityContextHolder.getContext()
                .getAuthentication()).getPrincipal();

        assert userId != null;
        UserModel user = userRepo.findById(UUID.fromString(userId))
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(GlobalAPIResponse.error("User not found"));
        }

        StudentModel student = null;
        StaffModel staff = null;

        switch (user.getRole()) {
            case student -> student = studentRepo.findByUserId(user.getId()).orElse(null);
            case staff -> staff = staffRepo.findByUserId(user.getId()).orElse(null);
        }

        AuthDTO authInfo = buildAuthDTO(user, student, staff);

        return ResponseEntity.ok(GlobalAPIResponse.success(authInfo));
    }

    @PostMapping("/logout")
    public ResponseEntity<GlobalAPIResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CookieUtil.getCookie(request, "refresh_token")
                .ifPresent(cookie -> {
                    refreshTokenService.revoke(cookie.getValue());
                });

        CookieUtil.deleteCookie(response, "refresh_token", "/api/v1/auth");

        return ResponseEntity.ok(GlobalAPIResponse.success(null, "Logged out successfully"));
    }

    private AuthDTO buildAuthDTO(UserModel user, StudentModel student, StaffModel staff) {
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

        if (student != null) {
            dto.setNim(student.getNim());
            dto.setStudyProgram(student.getStudyProgram());
        }

        if (staff != null) {
            dto.setNip(staff.getNip());
        }

        return dto;
    }
}
