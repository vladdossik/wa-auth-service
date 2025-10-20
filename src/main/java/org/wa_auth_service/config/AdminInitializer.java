package org.wa_auth_service.config;

import lombok.RequiredArgsConstructor;
import org.wa_auth_service.model.Role;
import org.wa_auth_service.model.Status;
import org.wa_auth_service.model.User;
import org.wa_auth_service.repository.RoleRepository;
import org.wa_auth_service.repository.UserRepository;
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
            Role adminRole = roleRepository.findByName("ADMIN");

            User admin = User.builder()
                    .email("admin@admin.com")
                    .password(passwordEncoder.encode("admin"))
                    .status(Status.ACTIVE)
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(admin);
        }
    }
}
