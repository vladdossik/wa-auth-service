package org.wa.auth.service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.wa.auth.service.service.AdminService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private static final String BLOCKED_USER_PREFIX = "blocked:user";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void blockUser(UUID externalId) {
        String key = BLOCKED_USER_PREFIX + externalId;
        redisTemplate.opsForValue().set(key, "blocked");
    }

    @Override
    public void unblockUser(UUID externalId) {
        String key = BLOCKED_USER_PREFIX + externalId;
        redisTemplate.delete(key);
    }

    @Override
    public boolean isBlocked(UUID externalId) {
        String key = BLOCKED_USER_PREFIX + externalId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
