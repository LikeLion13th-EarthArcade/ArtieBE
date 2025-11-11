package com.project.team5backend.domain.recommendation.dto.response;

import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public class RecommendationResDTO {
    @Builder
    public record PersonalizedSummaryResDTO(
            boolean eligible,
            Long exhibitionId,
            String title,
            String description,
            String thumbnail,
            String category,
            String mood,
            String location,
            LocalDate startDate,
            LocalDate endDate,
            double reviewAvg,
            int reviewCount,
            Boolean isLiked
    ) {
        public static PersonalizedSummaryResDTO empty() {
            return new RecommendationResDTO.PersonalizedSummaryResDTO(false, null, null, null,null,null,null,null,null,null,0.0,0,null);
        }
    }

    @Builder
    public record PersonalizedDetailResDTO(
            String topExhibitionCategory,
            String topExhibitionType,
            String topExhibitionMood,
            List<ExhibitionResDTO.ExhibitionCardResDTO> items
    ) {
        public static PersonalizedDetailResDTO empty() {
            return new PersonalizedDetailResDTO(null,null, null, List.of());
        }
    }

    @Builder
    public record CachedRecommendationResDTO(
            List<Long> exhibitionIds,
            String topExhibitionCategory,
            String topExhibitionType,
            String topExhibitionMood
    ) {}
}
