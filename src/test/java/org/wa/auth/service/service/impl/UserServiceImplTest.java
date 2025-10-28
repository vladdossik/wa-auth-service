package org.wa.auth.service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.dto.UserUpdateDto;
import org.wa.auth.service.exception.RoleNotFoundException;
import org.wa.auth.service.mapper.UserMapper;
import org.wa.auth.service.model.Role;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.repository.RoleRepository;
import org.wa.auth.service.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserLookupServiceImpl userLookupService;
    @Mock
    private UserValidationServiceImpl userValidationService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPhone("88007006050");
        user.setPassword("encoded123");
        user.setRoles(new HashSet<>());
        user.setStatus(StatusEnum.ACTIVE);

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@test.com");
        userDto.setPhone("88007006050");
    }

    @Test
    void createUserSuccessTest() {
        UserCreateDto dto = new UserCreateDto();
        dto.setEmail("test@test.com");
        dto.setPhone("88007006050");
        dto.setPassword("12345");

        Role role = new Role();
        role.setName(RoleEnum.USER);

        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(role);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userValidationService).validateUniqueUser(dto.getPhone(), dto.getEmail());
        verify(passwordEncoder).encode(dto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void findAllUsersSuccessTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.findAllUsers();

        assertEquals(1, result.size());
        assertEquals(userDto.getEmail(), result.getFirst().getEmail());
    }

    @Test
    void findUserByIdSuccessTest() {
        when(userLookupService.findUserById(1L)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userLookupService).findUserById(1L);
    }

    @Test
    void updateUserSuccessTest() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setPhone("81002003040");
        dto.setEmail("newTest@test.com");
        dto.setPassword("newPassword");
        dto.setRoles(Set.of(RoleEnum.ADMIN));

        Role adminRole = new Role();
        adminRole.setName(RoleEnum.ADMIN);

        when(userLookupService.findUserById(1L)).thenReturn(user);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(adminRole);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("newEncoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, dto);

        assertNotNull(result);
        verify(userValidationService).validateUniqueUser(dto.getPhone(), dto.getEmail());
        verify(passwordEncoder).encode(dto.getPassword());
        verify(roleRepository).findByName(RoleEnum.ADMIN);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserRoleNotFoundTest() {
        UserUpdateDto dto = new UserUpdateDto();
        dto.setRoles(Set.of(RoleEnum.ADMIN));

        when(userLookupService.findUserById(1L)).thenReturn(user);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(null);

        assertThrows(RoleNotFoundException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void deleteUserSuccessTest() {
        when(userLookupService.findUserById(1L)).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
