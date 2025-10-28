package org.wa.auth.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.dto.UserUpdateDto;
import org.wa.auth.service.model.Role;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("test@test.com");
        user.setPhone("88007006050");
    }

    @Test
    void toDtoTest() {
        user.setId(1L);
        user.setRoles(Set.of(new Role(1L, RoleEnum.USER)));

        UserDto userDto = userMapper.toDto(user);

        assertNotNull(userDto);
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPhone(), userDto.getPhone());
        assertTrue(userDto.getRoles().contains(RoleEnum.USER));
    }

    @Test
    void toDtoNoRolesTest() {
        user.setId(1L);
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
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@test.com");
        dto.setPhone("88007006050");

        User user = userMapper.toEntity(dto);

        assertNull(user.getId());
        assertNull(user.getPassword());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getPhone(), user.getPhone());
    }

    @Test
    void toEntityNullTest() {
        assertNull(userMapper.toEntity(null));
    }

    @Test
    void updateUserFromDtoEmailTest() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("newTest@test.com");
        dto.setPhone(null);

        userMapper.updateUserFromDto(dto, user);

        assertNotNull(user.getPhone());
        assertEquals(dto.getEmail(), user.getEmail());
    }

    @Test
    void updateUserFromDtoPhoneTest() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail(null);
        dto.setPhone("81002003040");

        userMapper.updateUserFromDto(dto, user);

        assertNotNull(user.getEmail());
        assertEquals(dto.getPhone(), user.getPhone());
    }

    @Test
    void updateUserFromDtoStatusTest() {
        user.setStatus(StatusEnum.PENDING);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setStatus(StatusEnum.BLOCKED);

        userMapper.updateUserFromDto(dto, user);

        assertEquals(dto.getStatus(), user.getStatus());
    }

    @Test
    void updateUserFromDtoNullTest() {
        userMapper.updateUserFromDto(null, user);

        assertEquals("test@test.com", user.getEmail());
        assertEquals("88007006050", user.getPhone());
    }
}
