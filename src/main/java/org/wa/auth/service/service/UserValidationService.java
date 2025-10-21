package org.wa.auth.service.service;

public interface UserValidationService {
    void validateUniqueUser(String phone, String email);
}
