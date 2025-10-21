package org.wa.auth.service.config;

import lombok.RequiredArgsConstructor;
import org.wa.auth.service.model.Role;
import org.wa.auth.service.model.RoleEnum;
import org.wa.auth.service.model.StatusEnum;
import org.wa.auth.service.model.User;
import org.wa.auth.service.repository.RoleRepository;
import org.wa.auth.service.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
//    TODO remove class before production deploy (for testing usage only)

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@admin.com").isEmpty()) {
            Role adminRole = roleRepository.findByName(RoleEnum.ADMIN);

            User admin = User.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .status(StatusEnum.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
        }
    }
}
