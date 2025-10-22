package org.wa.auth.service.service;

import org.wa.auth.service.exception.UserAuthException;
import org.wa.auth.service.model.jwt.JwtRequest;
import org.wa.auth.service.model.jwt.JwtResponse;

public interface AuthService {
    JwtResponse login(JwtRequest authRequest) throws UserAuthException;
    JwtResponse getAccessToken(String refreshToken) throws UserAuthException;
    JwtResponse refresh(String refreshToken) throws UserAuthException;
}
