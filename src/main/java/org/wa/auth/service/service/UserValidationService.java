package org.wa.auth.service.service;

public interface UserValidationService {
    void validatePhone(String phone);
    void validateUniqueUser(String phone, String email);
}
