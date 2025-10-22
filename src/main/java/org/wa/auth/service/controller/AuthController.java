package org.wa.auth.service.controller;

import lombok.RequiredArgsConstructor;
import org.wa.auth.service.exception.UserAuthException;
import org.wa.auth.service.model.jwt.JwtRequest;
import org.wa.auth.service.model.jwt.JwtResponse;
import org.wa.auth.service.model.jwt.RefreshJwtRequest;
import org.wa.auth.service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) throws UserAuthException {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getAccessToken(@RequestBody RefreshJwtRequest request) throws UserAuthException {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getRefreshToken(@RequestBody RefreshJwtRequest request) throws UserAuthException {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
