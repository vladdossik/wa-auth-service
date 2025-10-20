package org.wa_auth_service.service;

public interface UserValidationService {
    void validatePhone(String phone);
    void validateUniqueUser(String phone, String email);
}
