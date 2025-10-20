package org.wa_auth_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.wa_auth_service.dto.UserCreateDto;
import org.wa_auth_service.dto.UserUpdateDto;
import org.wa_auth_service.dto.UserDto;
import org.wa_auth_service.mapper.UserMapper;
import org.wa_auth_service.model.Role;
import org.wa_auth_service.model.Status;
import org.wa_auth_service.model.User;
import org.wa_auth_service.repository.RoleRepository;
import org.wa_auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wa_auth_service.service.UserLookupService;
import org.wa_auth_service.service.UserService;
import org.wa_auth_service.service.UserValidationService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final UserLookupService userLookupService;
    private final UserValidationService userValidationService;

    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        userValidationService.validatePhone(dto.getPhone());
        userValidationService.validateUniqueUser(dto.getPhone(), dto.getEmail());

        Role userRole = roleRepository.findByName("USER");
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);
        user.setStatus(Status.PENDING);

        return userMapper.toUser(userRepository.save(user));
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUser).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userLookupService.findUserById(id);

        return userMapper.toUser(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto dto) {
        userValidationService.validatePhone(dto.getPhone());

        User user = userLookupService.findUserById(id);

        userMapper.updateUserFromDto(dto, user);

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

        return userMapper.toUser(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userLookupService.findUserById(id);
        userRepository.delete(user);
    }
}
