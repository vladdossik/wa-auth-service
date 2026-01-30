package org.wa.auth.service.service;

import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto user);
    List<UserDto> findAllUsers();
    UserDto getUserById(Long id);
    UserDto getUserByEmail(String email);
    UserDto updateUser(Long id, UserUpdateDto user);
    void deleteUser(Long id);
    void saveGoogleRefreshToken(String email, String refreshToken);
}
