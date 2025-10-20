package org.wa_auth_service.service;

import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.wa_auth_service.model.RefreshToken;
import org.wa_auth_service.model.User;
import org.wa_auth_service.model.jwt.JwtRequest;
import org.wa_auth_service.model.jwt.JwtResponse;
import org.wa_auth_service.repository.RefreshTokenRepository;
import org.wa_auth_service.repository.UserRepository;
import org.wa_auth_service.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshInterval;

    public JwtResponse login(@NonNull JwtRequest authRequest) throws AuthException {
        final User user = userRepository.findByEmail(authRequest.getLogin())
                .orElseThrow(() -> new AuthException("Пользователь не найден"));


        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Неправильный пароль");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshTokenValue = jwtService.generateRefreshToken(user);

        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(Instant.now().plusMillis(refreshInterval))
                .build();

        refreshTokenRepository.save(refreshToken);

        return new JwtResponse(accessToken, refreshTokenValue);
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) throws AuthException {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException("Токен не найден"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new AuthException("Токен просрочен");
        }

        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new AuthException("Некорректный токен");
        }

        String email = jwtService.getRefreshClaims(refreshToken).getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("Пользователь не найден"));

        String accessToken = jwtService.generateAccessToken(user);
        return new JwtResponse(accessToken, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws AuthException {
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthException("Токен не найден"));

        if (storedToken.getExpiresAt().isBefore(Instant.now()))
            throw new AuthException("Токен просрочен");

        if (!jwtService.validateRefreshToken(refreshToken))
            throw new AuthException("Некорректный токен");

        User user = storedToken.getUser();

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        storedToken.setToken(newRefreshToken);
        storedToken.setExpiresAt(Instant.now().plusMillis(refreshInterval));
        refreshTokenRepository.save(storedToken);

        return new JwtResponse(newAccessToken, newRefreshToken);
    }
}
