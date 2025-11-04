package com.project.team5backend.domain.admin.exhibition.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.domain.common.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AdminExhibitionResDTO {
    @Builder
    public record ExhibitionSummaryResDTO(
            Long exhibitionId,
            String title,
            LocalDateTime createdAt,
            Status status
    ) {}

    @Builder
    public record ExhibitionStatusUpdateResDTO(
            Long exhibitionId,
            Status status,
            String message
    ){}

    @Builder
    public record ExhibitionDetailResDTO(
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
}
