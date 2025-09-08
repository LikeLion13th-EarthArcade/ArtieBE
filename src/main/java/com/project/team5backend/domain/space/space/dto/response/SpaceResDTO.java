package com.project.team5backend.domain.space.space.dto.response;

import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
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
    public record CreateSpaceResDTO (
            Long id,
            LocalDateTime createdAt
    ){}

    // 상세 조회 DTO
    @Builder
    public record DetailSpaceResDTO (
            Long spaceId,
            String name,
            List<String> imageUrls,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            LocalTime openTime,
            LocalTime closeTime,
            SpaceSize spaceSize,
            SpacePurpose spacePurpose,
            SpaceMood spaceMood,
            String description,
            List<String> facilities,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl
    ){}

    @Builder
    public record LikeSpaceResDTO(
            Long spaceId,
            String message
    ){}

    // 페이지 결과 DTO
    public record SearchSpacePageResDTO(
            List<SearchSpaceResDTO> items,
            PageInfo pageInfo,
            MapInfo mapInfo
    ) {
        public record PageInfo(
                int number,
                int size,
                long totalElements,
                int totalPages,
                boolean first,
                boolean last
        ) {}

        public record MapInfo(
                Double latitude,
                Double longitude
        ) {}
    }
    // 검색 결과 DTO
    @Builder
    public record SearchSpaceResDTO (
            Long spaceId,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            LocalTime openTime,
            LocalTime closeTime,
            String thumbnail
    ){}
}