package com.project.team5backend.domain.recommendation.dto.response;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionMood;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RecommendResDTO {
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
            BigDecimal reviewAvg,
            int reviewCount,
            Boolean isLiked
    ) {}
    public record PersonalizedDetailResDTO(
            ExhibitionCategory topExhibitionCategory,
            ExhibitionMood topExhibitionMood,
            List<ExhibitionResDTO.ExhibitionCardResDTO> items
    ) {}
}
