package org.wa_auth_service.service;

import org.wa_auth_service.model.User;

public interface UserLookupService {
    User findUserById(Long id);
    User findUserByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
