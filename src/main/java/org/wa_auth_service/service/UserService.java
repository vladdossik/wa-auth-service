package org.wa_auth_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.wa_auth_service.dto.UserCreateDto;
import org.wa_auth_service.dto.UserUpdateDto;
import org.wa_auth_service.exception.UserAlreadyExistsException;
import org.wa_auth_service.dto.UserDto;
import org.wa_auth_service.exception.UserNotFoundException;
import org.wa_auth_service.model.Role;
import org.wa_auth_service.model.Status;
import org.wa_auth_service.model.User;
import org.wa_auth_service.repository.RoleRepository;
import org.wa_auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserDto createUser(UserCreateDto dto) {
        validatePhone(dto.getPhone());

        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new UserAlreadyExistsException("Пользователь с таким телефоном уже существует: " + dto.getPhone());
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с таким email уже существует: " + dto.getEmail());
        }
        Role userRole = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = User.builder()
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(passwordEncoder.encode(dto.getPassword()))
                .status(Status.PENDING)
                .roles(roles)
                .build();

        return mapToDto(userRepository.save(user));
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден: " + id));

        return mapToDto(user);
    }

    public UserDto updateUser(Long id, UserUpdateDto dto) {
        validatePhone(dto.getPhone());

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        user.setEmail(dto.getEmail() != null ? dto.getEmail() : user.getEmail());
        user.setPhone(dto.getPhone() != null ? dto.getPhone() : user.getPhone());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : user.getStatus());
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getRoles() != null) {
            Set<Role> roles = dto.getRoles().stream()
                    .map(roleName -> {
                        Role role = roleRepository.findByName(roleName);
                        if (role == null) {
                            throw new RuntimeException("Роль не найдена: " + roleName);
                        }
                        return role;
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return mapToDto(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
        userRepository.delete(user);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .status(user.getStatus())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet()))
                .build();
    }

    private void validatePhone(String phone) {
        if (phone != null && phone.length() > 11) {
            throw new IllegalArgumentException("Номер телефона не может содержать более 11 символов");
        }
    }
}
