package org.wa_auth_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.wa_auth_service.exception.UserAlreadyExistsException;
import org.wa_auth_service.service.UserLookupService;
import org.wa_auth_service.service.UserValidationService;

@Service
@RequiredArgsConstructor
public class UserValidationServiceImpl implements UserValidationService {
    private final UserLookupService userLookupService;

    public void validatePhone(String phone) {
        if (phone != null && phone.length() > 11) {
            throw new IllegalArgumentException("Номер телефона не может содержать более 11 символов");
        }
    }

    public void validateUniqueUser(String phone, String email) {
        if (phone != null && userLookupService.existsByPhone(phone)) {
            throw new UserAlreadyExistsException("Пользователь с таким телефоном уже существует: " + phone);
        }

        if (email != null && userLookupService.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует: " + email);
        }
    }
}
