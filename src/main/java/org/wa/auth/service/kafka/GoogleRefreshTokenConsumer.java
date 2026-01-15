package org.wa.auth.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.wa.auth.service.dto.GoogleRefreshTokenEvent;
import org.wa.auth.service.service.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class GoogleRefreshTokenConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "${kafka.topics.google-refresh-token}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "googleRefreshTokenKafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeGoogleRefreshToken(GoogleRefreshTokenEvent event) {
        try {
            String email = event.getEmail();
            String refreshToken = event.getRefreshToken();

            log.debug("Received Google refresh token for user: {}", email);

            userService.saveGoogleRefreshToken(email, refreshToken);

            log.debug("Successfully processed Google refresh token for user: {}", event.getEmail());

        } catch (Exception e) {
            log.error("Failed to process Google refresh token event for user: {}", event.getEmail(), e);
        }
    }
}
