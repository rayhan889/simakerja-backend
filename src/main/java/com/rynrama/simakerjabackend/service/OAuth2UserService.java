package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.model.*;
import com.rynrama.simakerjabackend.repository.LecturerRepository;
import com.rynrama.simakerjabackend.repository.OAuthIdentityRepository;
import com.rynrama.simakerjabackend.repository.StudentRepository;
import com.rynrama.simakerjabackend.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OAuth2UserService extends OidcUserService {

    private static final Logger log = LoggerFactory.getLogger(OAuth2UserService.class);
    private final UserRepository userRepository;
    private final OAuthIdentityRepository oauthIdentityRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;


    public OAuth2UserService(
            UserRepository userRepository,
            OAuthIdentityRepository oauthIdentityRepository,
            LecturerRepository lecturerRepository,
            StudentRepository studentRepository
    ) {
        this.userRepository = userRepository;
        this.oauthIdentityRepository = oauthIdentityRepository;
        this.lecturerRepository = lecturerRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest oidcUserRequest){
        OidcUser oidcUser = super.loadUser(oidcUserRequest);

        String sub = oidcUser.getSubject();

        OauthIdentityModel identity =
                oauthIdentityRepository.findByProviderAndProviderUserId("google", sub).
                    orElseGet(() -> createUser(oidcUser));

        UserModel user = identity.getUser();

        if (!"active".equals(user.getStatus())){
            throw new OAuth2AuthenticationException("User is inactive");
        }

        return oidcUser;
    }

    private OauthIdentityModel createUser(OidcUser oidcUser){
//        user(student or lecturer) creation
        UserModel user = new UserModel();
        user.setEmail(oidcUser.getEmail());
        user.setFullName(oidcUser.getFullName());
        user.setCreatedAt(Instant.now());

        if (!isMhs(oidcUser)) {
            user.setRole(UserRole.lecturer);
        } else {
            user.setRole(UserRole.student);
        }

        userRepository.save(user);

        if (user.getRole() == UserRole.lecturer) {
            LecturerModel lecturer = new LecturerModel();
            lecturer.setUser(user);

            lecturerRepository.save(lecturer);
        } else {
            StudentModel student = new StudentModel();
            student.setUser(user);

            studentRepository.save(student);
        }

//        OAuthIdentityModel creation
        OauthIdentityModel identity = new OauthIdentityModel();
        identity.setUser(user);
        identity.setProvider("google");
        identity.setProviderUserId(oidcUser.getSubject());
        identity.setProviderEmail(oidcUser.getEmail());
        identity.setCreatedAt(Instant.now());

        oauthIdentityRepository.save(identity);

        return identity;
    }

    private boolean isMhs(OidcUser oidcUser){
        String hd = oidcUser.getClaim("hd");
        return hd != null && hd.startsWith("mhs.");
    }
}
