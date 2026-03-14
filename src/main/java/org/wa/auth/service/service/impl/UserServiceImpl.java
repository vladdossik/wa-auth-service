package org.wa.auth.service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.wa.auth.service.dto.SyncServiceDto;
import org.wa.auth.service.dto.SyncServicePaginatedDto;
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
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
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
    @Value("${synchronization.page.size}")
    private Integer pageSize;

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

    public Flux<Object> streamAllUsers() {
        log.info("Синхронизация активных пользователей с наличием google refresh token");
        return Flux.generate(
                () -> 0,
                (page, sink) -> {
                    try {
                        Page<User> userPage = userRepository.findUserByStatusAndGoogleRefreshTokenIsNotNull(
                                StatusEnum.ACTIVE,
                                PageRequest.of(page, pageSize, Sort.by("id"))
                        );

                        if (!userPage.hasContent()) {
                            sink.complete();
                        }

                        List<SyncServiceDto> dtoList = userPage.getContent().stream()
                                .map(userMapper::toSyncServiceDto)
                                .filter(dto -> dto != null && dto.getId() != null).toList();

                        SyncServicePaginatedDto response = SyncServicePaginatedDto.builder()
                                .users(dtoList)
                                .currentPage(page)
                                .totalPages(userPage.getTotalPages())
                                .totalElements(userPage.getTotalElements())
                                .hasNext(userPage.hasNext())
                                .build();

                        sink.next(response);

                        if (!userPage.hasNext()) {
                            sink.complete();
                        }
                        return page + 1;
                    } catch (Exception e) {
                        sink.error(e);
                        return page;
                    }
                }
        ).delayElements(Duration.ofMillis(100))
                .onErrorResume(e -> {
                    log.error("Ошибка при потоковой передаче", e);
                    return Flux.empty();
                });
    }

    public UserDto getUserById(UUID externalId) {
        User user = userLookupService.findUserByExternalId(externalId);
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(UUID externalId, UserUpdateDto dto) {
        userValidationService.validateUniqueUser(dto.getPhone(), dto.getEmail());

        User user = userLookupService.findUserByExternalId(externalId);

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
    public void deleteUser(UUID externalId) {
        User user = userLookupService.findUserByExternalId(externalId);
        userRepository.delete(user);
    }

    @Transactional
    public void saveGoogleRefreshToken(final String email, final String refreshToken) {
        User user = userLookupService.findUserByEmail(email);
        user.setGoogleRefreshToken(refreshToken);
        userRepository.save(user);
    }
}
