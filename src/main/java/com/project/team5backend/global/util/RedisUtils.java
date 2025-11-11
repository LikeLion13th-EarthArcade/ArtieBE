package com.project.team5backend.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils<T> {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, T val, Long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    public boolean hasKey(String key) {
        return Objects.equals(Boolean.TRUE, redisTemplate.hasKey(key));
    }

    @SuppressWarnings("unchecked")
    public T get(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public <R> R executeLua(DefaultRedisScript<R> script, List<String> keys, Object... args) {
        return redisTemplate.execute(script, keys, args);
    }
}
