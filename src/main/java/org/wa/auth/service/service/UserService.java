package org.wa.auth.service.service;

import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.dto.UserUpdateDto;
import reactor.core.publisher.Flux;
import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateDto user);
    List<UserDto> findAllUsers();
    Flux<Object> streamAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserUpdateDto user);
    void deleteUser(Long id);
    void saveGoogleRefreshToken(String email, String refreshToken);
}
