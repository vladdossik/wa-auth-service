package org.wa.auth.service.service;

import org.wa.auth.service.model.User;

public interface UserLookupService {
    User findUserById(Long id);
    User findUserByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
}
