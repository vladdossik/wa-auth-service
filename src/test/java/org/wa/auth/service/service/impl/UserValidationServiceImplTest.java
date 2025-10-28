package org.wa.auth.service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.wa.auth.service.exception.UserAlreadyExistsException;
import org.wa.auth.service.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidationServiceImplTest {

    @Mock
    private UserLookupServiceImpl userLookupService;

    @InjectMocks
    private UserValidationServiceImpl userValidationService;


    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPhone("88007006050");
        user.setPassword("password");
    }

    @Test
    void validateUserPhoneFailureTest() {
        when(userLookupService.existsByPhone(user.getPhone())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userValidationService.validateUniqueUser(user.getPhone(), "newTest@test.com"));

        verify(userLookupService).existsByPhone(user.getPhone());
    }

    @Test
    void validateUserEmailFailureTest() {
        when(userLookupService.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userValidationService.validateUniqueUser("81002003040", user.getEmail()));

        verify(userLookupService).existsByEmail(user.getEmail());
    }
}
