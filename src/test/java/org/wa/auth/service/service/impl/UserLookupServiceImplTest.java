package org.wa.auth.service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wa.auth.service.exception.UserNotFoundException;
import org.wa.auth.service.model.User;
import org.wa.auth.service.repository.UserRepository;
import org.wa.auth.service.util.Initializer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserLookupServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserLookupServiceImpl userLookupService;

    private User user;

    @BeforeEach
    void setUp() {
        user = Initializer.createUser();
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userLookupService.findUserById(1L);

        assertNotNull(user);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void findUserByIdNotFoundTest() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userLookupService.findUserById(1L));
    }

    @Test
    void findUserByEmailTest() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = userLookupService.findUserByEmail("test@test.com");

        assertNotNull(user);
        assertEquals(1L, result.getId());
        verify(userRepository).findByEmail("test@test.com");
    }

    @Test
    void findUserByEmailNotFoundTest() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userLookupService.findUserByEmail("test@test.com"));
    }

    @Test
    void existsByPhoneTest() {
        when(userRepository.existsByPhone("88007006050")).thenReturn(true);

        boolean result = userLookupService.existsByPhone("88007006050");

        assertTrue(result);
        verify(userRepository).existsByPhone("88007006050");
    }

    @Test
    void existsByEmailTest() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        boolean result = userLookupService.existsByEmail("test@test.com");

        assertTrue(result);
        verify(userRepository).existsByEmail("test@test.com");
    }
}
