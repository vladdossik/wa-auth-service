package org.wa.auth.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wa.auth.service.exception.UserNotFoundException;
import org.wa.auth.service.model.User;
import org.wa.auth.service.repository.UserRepository;
import org.wa.auth.service.service.UserLookupService;

@Service
@RequiredArgsConstructor
public class UserLookupServiceImpl implements UserLookupService {
    private final UserRepository userRepository;

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    @Override
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findUserByExternalId(String externalId) {
        return userRepository.findByExternalId(externalId);
    }
}
