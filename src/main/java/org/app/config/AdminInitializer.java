package org.app.config;

import lombok.RequiredArgsConstructor;
import org.app.model.Role;
import org.app.model.Status;
import org.app.model.User;
import org.app.repository.RoleRepository;
import org.app.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {

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
