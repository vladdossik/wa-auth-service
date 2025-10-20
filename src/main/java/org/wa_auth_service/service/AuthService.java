package org.wa_auth_service.service;

import jakarta.security.auth.message.AuthException;
import org.wa_auth_service.model.jwt.JwtRequest;
import org.wa_auth_service.model.jwt.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest authRequest) throws AuthException;
    JwtResponse getAccessToken(String refreshToken) throws AuthException;
    JwtResponse refresh(String refreshToken) throws AuthException;
}
