package com.project.team5backend.global.infra.redis;

import com.project.team5backend.domain.recommendation.cache.RecommendationCachePort;
import com.project.team5backend.domain.recommendation.dto.response.RecommendationResDTO;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisRecommendationCacheAdapter implements RecommendationCachePort {

    private final RedisUtils<RecommendationResDTO.CachedRecommendationResDTO> redisUtils;

    private static final String KEY_PREFIX = "recommend:user:";
    private static final long TTL_HOURS = 24;

    @Override
    public void saveTopRecommendations(Long userId, RecommendationResDTO.CachedRecommendationResDTO exhibitionIds) {
        redisUtils.save(KEY_PREFIX + userId, exhibitionIds, TTL_HOURS, TimeUnit.HOURS);
    }

    @Override
    public RecommendationResDTO.CachedRecommendationResDTO getTopRecommendations(Long userId) {
        return redisUtils.get(KEY_PREFIX + userId);
    }

    @Override
    public void delete(Long userId) {
        redisUtils.delete(KEY_PREFIX + userId);
    }
}
