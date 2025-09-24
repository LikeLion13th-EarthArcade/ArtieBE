package com.project.team5backend.domain.exhibition.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            String operatingInfo,
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
    public record ExhibitionSummaryResDTO(
            Long exhibitionId,
            String title,
            LocalDateTime createdAt,
            Status status
    ) {}

    @Builder
    public record MyExhibitionDetailResDTO(
            Long exhibitionId,
            String title,
            String description,
            List<String> imageUrls,
            @Schema(example = "2025.06.30") @JsonFormat(pattern = "YYYY.MM.DD") LocalDate startDate,
            @Schema(example = "2025.06.30") @JsonFormat(pattern = "YYYY.MM.DD") LocalDate endDate,
            String operatingInfo,
            String detailAddress,
            String address,
            ExhibitionCategory exhibitionCategory,
            ExhibitionType exhibitionType,
            Integer price,
            String websiteUrl
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

    public record ExhibitionCrawlResDto(
            @JsonProperty("serviceName") String serviceName,
            @JsonProperty("seq") String portalExhibitionId,
            @JsonProperty("title") String title,
            @JsonProperty("startDate") String startDate,
            @JsonProperty("endDate") String endDate,
            @JsonProperty("place") String place,
            @JsonProperty("realmName") String realmName,
            @JsonProperty("area") String area,
            @JsonProperty("sigungu") String sigungu,
            @JsonProperty("thumbnail") String thumbnail,
            @JsonProperty("gpsX") Double gpsX,
            @JsonProperty("gpsY") Double gpsY
    ) {}

    public record ExhibitionEnumResDTO(
            ExhibitionCategory exhibitionCategory,
            ExhibitionType exhibitionType,
            ExhibitionMood exhibitionMood
    ) {}

}
