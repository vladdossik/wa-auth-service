package org.wa_auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wa_auth_service.model.RefreshToken;
import org.wa_auth_service.model.User;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
