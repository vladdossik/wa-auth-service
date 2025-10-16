package org.app.service;

import lombok.RequiredArgsConstructor;
import org.app.dto.UserCreateUpdateDto;
import org.app.exception.UserAlreadyExistsException;
import org.app.dto.UserDto;
import org.app.exception.UserNotFoundException;
import org.app.model.Role;
import org.app.model.Status;
import org.app.model.User;
import org.app.repository.RoleRepository;
import org.app.repository.UserRepository;
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

    public UserDto createUser(UserCreateUpdateDto dto) {
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

    public UserDto updateUser(Long id, UserCreateUpdateDto dto) {
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
        userRepository.deleteById(id);
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
}
