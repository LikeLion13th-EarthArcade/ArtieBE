package com.project.team5backend.domain.recommendation.converter;

import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.domain.recommendation.dto.response.RecommendationResDTO;
import com.project.team5backend.domain.recommendation.entity.ExhibitionInteractLog;
import com.project.team5backend.domain.recommendation.model.ActionType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RecommendationConverter {

    public static ExhibitionInteractLog toExhibitionInteractLog(Long userId, Long exhibitionId, ActionType actionType) {
        return ExhibitionInteractLog.builder()
                .userId(userId)
                .exhibitionId(exhibitionId)
                .actionType(actionType)
                .build();
    }

    public static RecommendationResDTO.PersonalizedSummaryResDTO toPersonalizedSummaryResDTO(Exhibition exhibition, boolean liked) {
        return RecommendationResDTO.PersonalizedSummaryResDTO.builder()
                .eligible(true)
                .exhibitionId(exhibition.getId())
                .title(exhibition.getTitle())
                .description(exhibition.getDescription())
                .thumbnail(exhibition.getThumbnail())
                .category(String.valueOf(exhibition.getExhibitionCategory()))
                .mood(String.valueOf(exhibition.getExhibitionMood()))
                .location(exhibition.getAddress().getRoadAddress())
                .startDate(exhibition.getStartDate())
                .endDate(exhibition.getEndDate())
                .reviewAvg(exhibition.getRatingAvg())
                .reviewCount(exhibition.getReviewCount())
                .isLiked(liked)
                .build();
    }

    public static RecommendationResDTO.PersonalizedDetailResDTO toPersonalizedDetailResDTO(String category, String type, String mood, List<ExhibitionResDTO.ExhibitionCardResDTO> cards) {
        return RecommendationResDTO.PersonalizedDetailResDTO.builder()
                .topExhibitionCategory(String.valueOf(category))
                .topExhibitionType(String.valueOf(type))
                .topExhibitionMood(String.valueOf(mood))
                .items(cards)
                .build();
    }

    public static RecommendationResDTO.CachedRecommendationResDTO toCachedRecommendationResDTO(
            List<Long> ids, ExhibitionCategory category, ExhibitionMood mood,  ExhibitionType type) {
        return RecommendationResDTO.CachedRecommendationResDTO.builder()
                .exhibitionIds(ids)
                .topExhibitionCategory(String.valueOf(category))
                .topExhibitionMood(String.valueOf(mood))
                .topExhibitionType(String.valueOf(type))
                .build();
    }
}
