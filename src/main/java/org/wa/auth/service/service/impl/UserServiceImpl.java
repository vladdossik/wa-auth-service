package org.wa.auth.service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.wa.auth.service.dto.UserCreateDto;
import org.wa.auth.service.dto.UserUpdateDto;
import org.wa.auth.service.dto.UserDto;
import org.wa.auth.service.exception.RoleNotFoundException;
import org.wa.auth.service.mapper.UserMapper;
import org.wa.auth.service.model.Role;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.producer.UserEventProducer;
import org.wa.auth.service.repository.RoleRepository;
import org.wa.auth.service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.wa.auth.service.service.EncryptService;
import org.wa.auth.service.service.UserLookupService;
import org.wa.auth.service.service.UserService;
import org.wa.auth.service.service.UserValidationService;

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
    private final UserEventProducer userEventProducer;
    private final EncryptService encryptService;

    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        userValidationService.validateUniqueUser(dto.getPhone(), dto.getEmail());

        Role userRole = roleRepository.findByName(RoleEnum.USER);
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(roles);
        user.setStatus(StatusEnum.PENDING);

        User savedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(savedUser);

        userEventProducer.sendUserRegisteredEvent(userMapper.toUserRegisteredDto(userDto, encryptService));

        return userDto;
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userLookupService.findUserById(id);

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto dto) {
        userValidationService.validateUniqueUser(dto.getPhone(), dto.getEmail());

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
                            throw new RoleNotFoundException("Роль не найдена: " + roleName);
                        }
                        return role;
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userLookupService.findUserById(id);
        userRepository.delete(user);
    }
}
