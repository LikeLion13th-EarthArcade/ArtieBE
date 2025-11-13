package com.project.team5backend.domain.recommendation.cache;

import com.project.team5backend.domain.recommendation.dto.response.RecommendationResDTO;


public interface RecommendationCachePort {
    void saveTopRecommendations(Long userId, RecommendationResDTO.CachedRecommendationResDTO dto);

    RecommendationResDTO.CachedRecommendationResDTO getTopRecommendations(Long userId);

    void delete(Long userId);
}
