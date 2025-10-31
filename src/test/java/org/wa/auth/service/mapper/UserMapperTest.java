package org.wa.auth.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.dto.UserUpdateDto;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.util.Initializer;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        user = Initializer.createUser();
        createDto = Initializer.createUserCreateDto();
        updateDto = Initializer.createUserUpdateDto();
    }

    @Test
    void toDtoTest() {
        user.setRoles(Set.of(Initializer.createRole(RoleEnum.USER)));

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhone(), userDto.getPhone());
        assertTrue(userDto.getRoles().contains(RoleEnum.USER));
    }

    @Test
    void toDtoNoRolesTest() {
        user.setRoles(null);

        UserDto userDto = userMapper.toDto(user);

        assertNull(userDto.getRoles());
    }

    @Test
    void toDtoNullTest() {
        assertNull(userMapper.toDto(null));
    }

    @Test
    void toEntityTest() {
        User user = userMapper.toEntity(createDto);

        assertNull(user.getId());
        assertNull(user.getPassword());
        assertEquals(createDto.getEmail(), user.getEmail());
        assertEquals(createDto.getPhone(), user.getPhone());
    }

    @Test
    void toEntityNullTest() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void updateUserFromDtoEmailTest() {
        updateDto.setPhone(null);

        userMapper.updateUserFromDto(updateDto, user);

        assertNotNull(user.getPhone());
        assertEquals(updateDto.getEmail(), user.getEmail());
    }

    @Test
    void updateUserFromDtoPhoneTest() {
        updateDto.setEmail(null);

        userMapper.updateUserFromDto(updateDto, user);

        assertNotNull(user.getEmail());
        assertEquals(updateDto.getPhone(), user.getPhone());
    }

    @Test
    void updateUserFromDtoStatusTest() {
        user.setStatus(StatusEnum.PENDING);
        updateDto.setStatus(StatusEnum.BLOCKED);

        userMapper.updateUserFromDto(updateDto, user);

        assertEquals(updateDto.getStatus(), user.getStatus());
    }

    @Test
    void updateUserFromDtoNullTest() {
        userMapper.updateUserFromDto(null, user);

        assertEquals("test@test.com", user.getEmail());
        assertEquals("88007006050", user.getPhone());
    }
}
