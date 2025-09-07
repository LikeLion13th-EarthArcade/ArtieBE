package com.project.team5backend.domain.space.space.dto.response;

import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;
import com.project.team5backend.global.entity.enums.Facility;
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
            Long exhibitionSpaceId,
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
            List<Facility> facility,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl
    ){}

    // 검색 결과 DTO
    @Builder
    public record SpaceSearchResponse (
            Long id,
            String name,
            String address,
            BigDecimal latitude,
            BigDecimal longitude,
            String startDate,
            String endDate,
            String thumbnail
    ){}
    // 페이지 결과 DTO
    public record SpaceSearchPageResponse(
            List<SpaceSearchResponse> items,
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
}