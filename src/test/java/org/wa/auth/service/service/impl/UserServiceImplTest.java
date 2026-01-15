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
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.producer.UserEventProducer;
import org.wa.auth.service.repository.RoleRepository;
import org.wa.auth.service.repository.UserRepository;
import org.wa.auth.service.util.Initializer;

import java.util.List;

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
    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserCreateDto createDto;
    private UserUpdateDto updateDto;

    @BeforeEach
    void setUp() {
        user = Initializer.createUser();
        userDto = Initializer.createUserDto();
        createDto = Initializer.createUserCreateDto();
        updateDto = Initializer.createUserUpdateDto();
    }

    @Test
    void createUserSuccessTest() {
        when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Initializer.createRole(RoleEnum.USER));
        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(passwordEncoder.encode(createDto.getPassword())).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(createDto);

        assertNotNull(result);
        assertEquals(createDto.getEmail(), result.getEmail());
        verify(userValidationService).validateUniqueUser(createDto.getPhone(), createDto.getEmail());
        verify(passwordEncoder).encode(createDto.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userEventProducer).sendUserRegisteredEvent(any());
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
        when(userLookupService.findUserById(1L)).thenReturn(user);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(Initializer.createRole(RoleEnum.ADMIN));
        when(passwordEncoder.encode(updateDto.getPassword())).thenReturn("newEncoded123");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        verify(userValidationService).validateUniqueUser(updateDto.getPhone(), updateDto.getEmail());
        verify(passwordEncoder).encode(updateDto.getPassword());
        verify(roleRepository).findByName(RoleEnum.ADMIN);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserRoleNotFoundTest() {
        when(userLookupService.findUserById(1L)).thenReturn(user);
        when(roleRepository.findByName(RoleEnum.ADMIN)).thenReturn(null);

        assertThrows(RoleNotFoundException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void deleteUserSuccessTest() {
        when(userLookupService.findUserById(1L)).thenReturn(user);

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }
}
