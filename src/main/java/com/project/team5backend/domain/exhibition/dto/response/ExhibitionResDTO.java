package com.project.team5backend.domain.exhibition.dto.response;

import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.util.PageResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class ExhibitionResDTO {

    @Builder
    public record ExhibitionCreateResDTO(
            Long exhibitionId,
            LocalDateTime createdAt
    ){}

    @Builder
    public record ExhibitionDetailResDTO (
            Long exhibitionId,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            String operatingHours,
            List<String> imageUrls,
            String websiteUrl,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            ExhibitionCategory exhibitionCategory,
            ExhibitionType exhibitionType,
            ExhibitionMood exhibitionMood,
            Integer price,
            List<String> facilities
    ) {}
    @Builder
    public record DetailPendingExhibitionResDTO (
            Long exhibitionId,
            String title,
            String description,
            LocalDate startDate,
            LocalDate endDate,
            LocalTime openTime,
            LocalTime closeTime,
            List<String> imageFileKeys,
            String websiteUrl,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            ExhibitionCategory exhibitionCategory,
            ExhibitionType exhibitionType,
            ExhibitionMood exhibitionMood,
            Integer price,
            List<String> facilities
    ) {}

    @Builder
    public record ExhibitionLikeResDTO(
            Long exhibitionId,
            String message
    ){}

    @Builder
    public record ExhibitionSearchResDTO (
            Long exhibitionId,
            String title,
            String thumbnail,
            LocalDate startDate,
            LocalDate endDate,
            String address,
            BigDecimal latitude,
            BigDecimal longitude
    ){ }

    @Builder
    public record ExhibitionSearchPageResDTO(
            PageResponse<ExhibitionSearchResDTO> page,
            MapInfo map
    ){
        public record MapInfo(
                Double defaultCenterLat,
                Double defaultCenterLng
        ) {}
    }

    @Builder
    public record ExhibitionHotNowResDTO(
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
    ){}

    @Builder
    public record UpcomingPopularExhibitionResDTO(
            Long exhibitionId,
            String title,
            List<String> imagesUrls
    ){}

    @Builder
    public record RegionalPopularExhibitionResDTO(
            Long exhibitionId,
            String district,
            String title,
            String thumbnail
    ){}

    @Builder
    public record RegionalPopularExhibitionListResDTO(
            List<RegionalPopularExhibitionResDTO> exhibitions
    ){}

    @Builder
    public record ArtieRecommendationResDTO(
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
    ){}

    @Builder
    public record ExhibitionCardResDTO(
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
    ){}
}
