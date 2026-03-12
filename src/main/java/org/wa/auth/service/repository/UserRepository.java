package org.wa.auth.service.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Page<User> findUserByStatusAndGoogleRefreshTokenIsNotNull(StatusEnum status, @NonNull Pageable pageable);

    User findByExternalId(String externalId);
}
