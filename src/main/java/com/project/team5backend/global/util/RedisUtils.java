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

    private final RedisTemplate<String, T> defaultRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public void save(String key, T val, Long time, TimeUnit timeUnit) {
        defaultRedisTemplate.opsForValue().set(key, val, time, timeUnit);
    }

    public boolean hasKey(String key) {
        return Objects.equals(Boolean.TRUE, defaultRedisTemplate.hasKey(key));
    }

    public T get(String key) {
        return defaultRedisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        defaultRedisTemplate.delete(key);
    }

    public <R> R executeLua(DefaultRedisScript<R> script, List<String> keys, Object... args) {
        return stringRedisTemplate.execute(script, keys, args);
    }
}
