package com.project.team5backend.domain.recommendation.service.query;

import com.project.team5backend.domain.recommendation.dto.response.RecommendationResDTO;

public interface RecommendationQueryService {
    RecommendationResDTO.PersonalizedSummaryResDTO getPersonalizedSummary(Long userId);

    RecommendationResDTO.PersonalizedDetailResDTO getPersonalizedDetail(Long userId);
}
