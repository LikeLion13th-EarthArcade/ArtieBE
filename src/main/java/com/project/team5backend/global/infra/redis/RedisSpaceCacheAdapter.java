package com.project.team5backend.global.infra.redis;

import com.project.team5backend.domain.space.cache.SpaceCachePort;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.project.team5backend.global.constant.redis.RedisConstant.KEY_SCOPE_SUFFIX;
import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_BIZ_NUMBER;

@Component
@RequiredArgsConstructor
public class RedisSpaceCacheAdapter implements SpaceCachePort {

    private final RedisUtils<String> redisUtils;

    @Override
    public boolean isValidated(String bizNumber) {
        return SCOPE_BIZ_NUMBER.equals(redisUtils.get(bizNumber + KEY_SCOPE_SUFFIX));
    }

    @Override
    public void invalidate(String bizNumber) {
        redisUtils.delete(bizNumber + KEY_SCOPE_SUFFIX);
    }
}
