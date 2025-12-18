package org.wa.auth.service.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.wa.auth.service.dto.UserRegisteredDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {

    private final KafkaTemplate<String, UserRegisteredDto> kafkaTemplate;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    public void sendUserRegisteredEvent(UserRegisteredDto userRegisteredEvent) {
        log.info("Sending USER_REGISTERED event: {}", userRegisteredEvent);

        kafkaTemplate.send(userRegisteredTopic, userRegisteredEvent.getId().toString(), userRegisteredEvent);
    }
}
