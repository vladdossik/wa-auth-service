package org.wa.auth.service.util;

import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserUpdateDto;
import org.wa.auth.service.model.RefreshToken;
import org.wa.auth.service.model.Role;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.model.jwt.JwtRequest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Set;

public class Initializer {

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPhone("88007006050");
        user.setPassword("encoded123");
        user.setStatus(StatusEnum.ACTIVE);

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleEnum.USER);
        user.setRoles(Set.of(role));

        return user;
    }

    public static UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("test@test.com");
        dto.setPhone("88007006050");
        dto.setRoles(Set.of(RoleEnum.USER));
        dto.setStatus(StatusEnum.ACTIVE);
        return dto;
    }

    public static UserCreateDto createUserCreateDto() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@test.com");
        dto.setPhone("88007006050");
        dto.setPassword("password123");
        return dto;
    }

    public static UserUpdateDto createUserUpdateDto() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("newTest@test.com");
        dto.setPhone("81002003040");
        dto.setPassword("newPassword");
        dto.setRoles(Set.of(RoleEnum.ADMIN));
        return dto;
    }

    public static Role createRole(RoleEnum roleEnum) {
        Role role = new Role();
        role.setId(1L);
        role.setName(roleEnum);
        return role;
    }

    public static JwtRequest createJwtRequest() {
        return new JwtRequest("test@test.com", "password");
    }

    public static RefreshToken createValidRefreshToken(User user) {
        return RefreshToken.builder()
                .token("valid-refresh")
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(1))
                .build();
    }

    public static RefreshToken createExpiredRefreshToken(User user) {
        return RefreshToken.builder()
                .token("expired")
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1))
                .build();
    }

    public static RefreshToken createInvalidRefreshToken(User user) {
        return RefreshToken.builder()
                .token("invalid")
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(1))
                .build();
    }

    public static RefreshToken createRefreshInitialToken(User user) {
        return RefreshToken.builder()
                .token("refresh-initial")
                .user(user)
                .expiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusHours(1))
                .build();
    }
}
