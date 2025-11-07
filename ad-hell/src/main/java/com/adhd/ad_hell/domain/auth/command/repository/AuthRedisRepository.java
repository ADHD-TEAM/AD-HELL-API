package com.adhd.ad_hell.domain.auth.command.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthRedisRepository {

    private final StringRedisTemplate redis;

    private static String refreshKey(String username) {
        return "refresh:%s".formatted(username);
    }
    private static String blKey(String jti) { return "bl:access:%s".formatted(jti); }

    public void saveRefresh(String username, String refreshToken, Duration ttl) {
        redis.opsForValue().set(refreshKey(username), refreshToken, ttl);
    }

    public Optional<String> getRefresh(String username) {
        String val = redis.opsForValue().get(refreshKey(username));
        return Optional.ofNullable(val);
    }

    public void deleteRefresh(String username) {
        redis.delete(refreshKey(username));
    }

    public void blacklistAccessJti(String jti, Duration ttl) {
        redis.opsForValue().set(blKey(jti), "1", ttl);
    }

    public boolean isBlacklisted(String jti) {
        return redis.hasKey(blKey(jti));
    }
}