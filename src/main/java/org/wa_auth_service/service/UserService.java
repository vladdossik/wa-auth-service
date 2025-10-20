package org.wa_auth_service.service;

import org.wa_auth_service.dto.UserCreateDto;
import org.wa_auth_service.dto.UserDto;
import org.wa_auth_service.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto user);
    List<UserDto> findAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserUpdateDto user);
    void deleteUser(Long id);
}
