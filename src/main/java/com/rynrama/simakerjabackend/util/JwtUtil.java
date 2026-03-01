package com.rynrama.simakerjabackend.util;

import com.rynrama.simakerjabackend.model.StaffModel;
import com.rynrama.simakerjabackend.model.StudentModel;
import com.rynrama.simakerjabackend.model.UserModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret}")
    private String jwtSecret;

//    in ms
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${spring.application.name}")
    private String appName;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public record JwtClaim(
            String userId,
            String email,
            String role,
            String fullName,
            String nim,
            String nip
    ){}

    public String generateToken(
        UserModel user,
        StudentModel student,
        StaffModel staff,
        String profilePicture
    ) {
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());
        claims.put("fullName", user.getFullName());

        if (user.getPhoneNumber() != null) {
            claims.put("phoneNumber", user.getPhoneNumber());
        }
        if (user.getStatus() != null) {
            claims.put("status", user.getStatus());
        }
        if (profilePicture != null) {
            claims.put("profilePicture", profilePicture);
        }

//        student config
        if (student != null) {
            if (student.getNim() != null) {
                claims.put("nim", student.getNim());
            }
            if (student.getStudyProgram() != null) {
                claims.put("studyProgram", student.getStudyProgram());
            }
        }

//        staff config
        if (staff != null) {
            if (staff.getNip() != null) {
                claims.put("nip", staff.getNip());
            }
        }

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setIssuer(appName)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }

    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public JwtClaim extractClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new JwtClaim(
                claims.getSubject(),
                claims.get("email", String.class),
                claims.get("role", String.class),
                claims.get("fullName", String.class),
                claims.get("nim", String.class),
                claims.get("nip", String.class)
        );
    }
}
