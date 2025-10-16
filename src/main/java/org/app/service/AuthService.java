package org.app.service;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.app.model.User;
import org.app.model.jwt.JwtRequest;
import org.app.model.jwt.JwtResponse;
import org.app.repository.UserRepository;
import org.app.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(@NonNull JwtRequest authRequest) throws AuthException {
        final Optional<User> user = userRepository.findByEmail(authRequest.getLogin());
        if (user.isEmpty()) {
            throw new AuthException("Пользователь не найден");
        }
        if (passwordEncoder.matches(authRequest.getPassword(), user.get().getPassword())) {
            final String accessToken = jwtService.generateAccessToken(user.get());
            final String refreshToken = jwtService.generateRefreshToken(user.get());
            refreshStorage.put(accessToken, refreshToken);
            return new JwtResponse(accessToken, refreshToken);
        } else {
            throw new AuthException("Неправильный пароль");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) throws AuthException {
        if (jwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Optional<User> user = userRepository.findByEmail(email);
                if (user.isEmpty()) {
                    throw new AuthException("Пользователь не найден");
                }
                final String accessToken = jwtService.generateAccessToken(user.get());
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) throws AuthException {
        if (jwtService.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtService.getRefreshClaims(refreshToken);
            final String email = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(email);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Optional<User> user = userRepository.findByEmail(email);
                if (user.isEmpty()) {
                    throw new AuthException("Пользователь не найден");
                }
                final String accessToken = jwtService.generateAccessToken(user.get());
                final String newRefreshToken = jwtService.generateRefreshToken(user.get());
                refreshStorage.put(email, newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new AuthException("Невалидный JWT токен");
    }
}
