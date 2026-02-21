package com.rynrama.simakerjabackend.config.security;

import com.rynrama.simakerjabackend.model.StaffModel;
import com.rynrama.simakerjabackend.model.StudentModel;
import com.rynrama.simakerjabackend.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomUserPrincipal implements OidcUser {

    private final OidcUser oidcUser;
    private final UserModel user;
    private final Set<GrantedAuthority> authorities;

    private final StudentModel student;
    private final StaffModel staff;

    public CustomUserPrincipal(
            OidcUser oidcUser,
            UserModel user,
            StudentModel student,
            StaffModel staff
    ) {
        this.oidcUser = oidcUser;
        this.user = user;
        this.authorities = buildAuthorities();
        this.student = student;
        this.staff = staff;
    }

    private Set<GrantedAuthority> buildAuthorities() {
        Set<GrantedAuthority> auths = new HashSet<>();

        String roleName = "ROLE_" + user.getRole().name().toUpperCase();
        System.out.println("roleName: " + roleName);
        auths.add(new SimpleGrantedAuthority(roleName));

        return auths;
    }

    public UserModel getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public String getName() {
        return oidcUser.getName();
    }

    public String getEmail() {
        return oidcUser.getEmail();
    }

    public String getFullName() {
        return oidcUser.getFullName();
    }

    public String getSubject() {
        return oidcUser.getSubject();
    }

    public StudentModel getStudent() {
        return student;
    }

    public StaffModel getStaff() {
        return staff;
    }

    public String getNim() {
        return student != null ? student.getNim() : null;
    }

    public String getStudyProgram() {
        return student != null ? student.getStudyProgram() : null;
    }

    public String getNip() {
        return staff != null ? staff.getNip() : null;
    }
}
