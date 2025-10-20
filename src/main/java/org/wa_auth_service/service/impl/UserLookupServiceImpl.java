package org.wa_auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wa_auth_service.exception.UserNotFoundException;
import org.wa_auth_service.model.User;
import org.wa_auth_service.repository.UserRepository;
import org.wa_auth_service.service.UserLookupService;

@Service
@RequiredArgsConstructor
public class UserLookupServiceImpl implements UserLookupService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + email));
    }

    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
