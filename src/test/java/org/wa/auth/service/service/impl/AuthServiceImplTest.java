package org.wa.auth.service.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.wa.auth.service.exception.UserAuthException;
import org.wa.auth.service.model.RefreshToken;
import org.wa.auth.service.model.User;
import org.wa.auth.service.model.jwt.JwtRequest;
import org.wa.auth.service.model.jwt.JwtResponse;
import org.wa.auth.service.repository.RefreshTokenRepository;
import org.wa.auth.service.security.JwtService;
import org.wa.auth.service.service.UserLookupService;
import org.wa.auth.service.util.Initializer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserLookupService userLookupService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private JwtRequest jwtRequest;

    @BeforeEach
    void setUp() {
        user = Initializer.createUser();
        jwtRequest = Initializer.createJwtRequest();
    }


    @Test
    void loginTokensSuccessTest() throws UserAuthException {
        when(userLookupService.findUserByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encoded123")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access123");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh123");
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());

        JwtResponse response = authService.login(jwtRequest);

        assertNotNull(response);
        assertEquals("access123", response.getAccessToken());
        assertEquals("refresh123", response.getRefreshToken());

        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void loginPasswordFailureTest() {
        when(userLookupService.findUserByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.matches("password", "encoded123")).thenReturn(false);

        assertThrows(UserAuthException.class, () -> authService.login(jwtRequest));
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void accessTokenSuccessTest() throws UserAuthException {
        RefreshToken storedToken = Initializer.createValidRefreshToken(user);

        when(refreshTokenRepository.findByToken("valid-refresh")).thenReturn(Optional.of(storedToken));
        when(jwtService.validateRefreshToken("valid-refresh")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access123");

        JwtResponse response = authService.getAccessToken("valid-refresh");

        assertNotNull(response);
        assertEquals("access123", response.getAccessToken());
        assertNull(response.getRefreshToken());
    }

    @Test
    void accessTokenNotFoundTest() {
        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.empty());

        assertThrows(UserAuthException.class, () -> authService.getAccessToken("expired"));
    }

    @Test
    void accessTokenExpiredTest() {
        RefreshToken expiredToken = Initializer.createExpiredRefreshToken(user);

        when(refreshTokenRepository.findByToken("expired")).thenReturn(Optional.of(expiredToken));

        assertThrows(UserAuthException.class, () -> authService.getAccessToken("expired"));
    }

    @Test
    void accessTokenValidityTest(){
        RefreshToken invalidToken = Initializer.createInvalidRefreshToken(user);

        when(refreshTokenRepository.findByToken("invalid")).thenReturn(Optional.of(invalidToken));
        when(jwtService.validateRefreshToken("invalid")).thenReturn(false);

        assertThrows(UserAuthException.class, () -> authService.getAccessToken("invalid"));
    }

    @Test
    void refreshTokenSuccessTest() throws UserAuthException {
        RefreshToken storedToken = Initializer.createRefreshInitialToken(user);

        when(refreshTokenRepository.findByToken("refresh-initial")).thenReturn(Optional.of(storedToken));
        when(jwtService.validateRefreshToken("refresh-initial")).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-new");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-new");

        JwtResponse response = authService.refresh("refresh-initial");

        assertEquals("access-new", response.getAccessToken());
        assertEquals("refresh-new", response.getRefreshToken());
        verify(refreshTokenRepository).save(storedToken);
    }
}
