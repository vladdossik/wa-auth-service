package org.wa.auth.service.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.wa.auth.service.exception.UserAuthException;
import org.wa.auth.service.model.RefreshToken;
import org.wa.auth.service.model.User;
import org.wa.auth.service.model.jwt.JwtRequest;
import org.wa.auth.service.model.jwt.JwtResponse;
import org.wa.auth.service.repository.RefreshTokenRepository;
import org.wa.auth.service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wa.auth.service.service.AuthService;
import org.wa.auth.service.service.UserLookupService;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshInterval;
    private final UserLookupService userLookupService;

    public JwtResponse login(@NonNull JwtRequest authRequest) throws UserAuthException {
        final User user = userLookupService.findUserByEmail(authRequest.getLogin());

        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new UserAuthException("Неправильный пароль");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        saveRefreshToken(user, refreshTokenValue);

        return new JwtResponse(accessToken, refreshTokenValue);
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) throws UserAuthException {
        RefreshToken storedToken = validateAndGetRefreshToken(refreshToken);
        User user = storedToken.getUser();

        String accessToken = jwtService.generateAccessToken(user);
        return new JwtResponse(accessToken, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws UserAuthException {
        RefreshToken storedToken = validateAndGetRefreshToken(refreshToken);
        User user = storedToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        storedToken.setToken(newRefreshToken);
        storedToken.setExpiresAt(expirationTime());
        refreshTokenRepository.save(storedToken);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }

    private void saveRefreshToken(User user, String refreshTokenValue) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(expirationTime())
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private OffsetDateTime expirationTime() {
        return OffsetDateTime.now(ZoneOffset.UTC).plus(Duration.ofMillis(refreshInterval));
    }

    private RefreshToken validateAndGetRefreshToken(String refreshToken) throws UserAuthException {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new UserAuthException("Токен не найден"));

        if (storedToken.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new UserAuthException("Токен просрочен");
        }

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new UserAuthException("Некорректный токен");
        }

        return storedToken;
    }
}
