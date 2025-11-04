package com.project.team5backend.domain.recommendation.converter;

import com.project.team5backend.domain.recommendation.entity.ExhibitionInteractLog;
import com.project.team5backend.domain.recommendation.model.ActionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationConverter {

    public static ExhibitionInteractLog toExhibitionInteractLog(Long userId, Long exhibitionId, ActionType actionType) {
        return ExhibitionInteractLog.builder()
                .userId(userId)
                .exhibitionId(exhibitionId)
                .actionType(actionType)
                .build();
    }
}
