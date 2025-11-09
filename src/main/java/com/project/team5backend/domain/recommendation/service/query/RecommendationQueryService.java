package com.project.team5backend.domain.recommendation.service.query;

import com.project.team5backend.domain.recommendation.dto.response.RecommendResDTO;

public interface RecommendationQueryService {
    RecommendResDTO.PersonalizedSummaryResDTO getPersonalizedSummary(Long userId);

    RecommendResDTO.PersonalizedDetailResDTO getPersonalizedDetail(Long userId);
}
