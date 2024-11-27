package com.example.hrm_be.common.utils;

import com.example.hrm_be.commons.enums.RoleType;
import com.example.hrm_be.models.entities.RoleEntity;
import com.example.hrm_be.models.entities.UserEntity;
import com.example.hrm_be.models.entities.UserRoleMapEntity;
import java.io.Serial;
import java.util.Collection;
import java.util.Collections;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestUtils {

    public static UserEntity initTestUserEntity(@NonNull PasswordEncoder passwordEncoder) {
        return UserEntity.builder()
                .userName("chuduong1811")
                .email("duongcdhe176312@gmail.com")
                .phone("0915435790")
                .firstName("chu")
                .lastName("duong")
                .password(passwordEncoder.encode("Abcd1234"))
                .build();
    }

    public static RoleEntity initTestRoleEntity(@NonNull RoleType type) {
        return RoleEntity.builder().name(type.getValue()).type(type).build();
    }

    public static UserRoleMapEntity initTestUserRoleMapEntity(@NonNull UserEntity user, @NonNull RoleEntity role) {
        return UserRoleMapEntity.builder().user(user).role(role).build();
    }

    public static void mockAuthenticatedUser(String email, RoleType roleType) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        UserDetails mockUserDetails = new User(
                email,
                "",
                Collections.singletonList(new SimpleGrantedAuthority(roleType.getValue()))
        );

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                mockUserDetails,
                null,
                mockUserDetails.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }

}

