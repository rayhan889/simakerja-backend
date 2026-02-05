package com.rynrama.simakerjabackend.service;

import com.rynrama.simakerjabackend.model.OauthIdentityModel;
import com.rynrama.simakerjabackend.model.UserModel;
import com.rynrama.simakerjabackend.model.UserRole;
import com.rynrama.simakerjabackend.repository.OAuthIdentityRepository;
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


    public OAuth2UserService(UserRepository userRepository, OAuthIdentityRepository oauthIdentityRepository) {
        this.userRepository = userRepository;
        this.oauthIdentityRepository = oauthIdentityRepository;
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
//        UserModel creation
        UserModel user = new UserModel();
        user.setEmail(oidcUser.getEmail());
        user.setFullName(oidcUser.getFullName());
//        check whether user is a student or lecturer
        if (!isMhs(oidcUser)){
            user.setRole(UserRole.lecturer);
        } else {
            user.setRole(UserRole.student);
        }
        user.setCreatedAt(Instant.now());

        userRepository.save(user);

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
