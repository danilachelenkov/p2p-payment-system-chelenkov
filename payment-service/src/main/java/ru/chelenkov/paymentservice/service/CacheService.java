package ru.chelenkov.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
        log.debug("Redis SET key={} value{} ttl={}", key, value, duration);
    }

    public Object get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        log.debug("Redis GET key={} value={}", key, value);
        return value;
    }

    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Redis DELETE key={}", key);
    }

    public boolean exists(String key) {
        Boolean isExists = redisTemplate.hasKey(key);
        log.debug("Redis exists key={} isExists={}", key, isExists);
        return isExists;
    }

    public void expire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
        log.debug("Redis expire key={} duration={}", key, duration);
    }

}
