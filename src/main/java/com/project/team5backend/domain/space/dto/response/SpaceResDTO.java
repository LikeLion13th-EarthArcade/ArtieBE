package com.project.team5backend.domain.space.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class SpaceResDTO {

    // 공간 등록 DTO
    @Builder
    public record SpaceCreateResDTO(
            Long id,
            LocalDateTime createdAt
    ){}

    // 상세 조회 DTO
    @Builder
    public record SpaceDetailResDTO(
            Long spaceId,
            String name,
            List<String> imageUrls,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String operatingHours,
            SpaceSize spaceSize,
            SpaceMood spaceMood,
            String description,
            List<String> facilities,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl
    ){}

    @Builder
    public record SpaceLikeResDTO(
            Long spaceId,
            String message
    ){}

    // 페이지 결과 DTO
    @Builder
    public record SpaceSearchPageResDTO(
            PageResponse<SpaceSearchResDTO> page,
            MapInfo map
    ) {
        public record MapInfo(
                Double latitude,
                Double longitude
        ) {}
    }

    // 검색 결과 DTO
    @Builder
    public record SpaceSearchResDTO(
            Long spaceId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String operatingHours,
            String thumbnail
    ){}
}