package com.lingcast.server.domain.auth.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;

    /**
     * 사용자별 Refresh Token을 Redis에 저장
     */
    public void save(Long userId, String refreshToken, long expirationSeconds) {

        String key = KEY_PREFIX + userId;

        // Refresh Token 만료시간과 동일하게 Redis 데이터도 자동 삭제되도록 TTL 설정
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                Duration.ofSeconds(expirationSeconds)
        );
    }

    /**
     * 사용자 ID에 해당하는 Refresh Token 조회
     */
    public Optional<String> findByUserId(Long userId) {

        String key = KEY_PREFIX + userId;
        String refreshToken = redisTemplate.opsForValue().get(key);

        return Optional.ofNullable(refreshToken);
    }

    /**
     * 로그아웃 등에 사용할 Refresh Token 삭제
     */
    public void deleteByUserId(Long userId) {

        String key = KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}