package com.project.team5backend.domain.space.dto.response;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.global.util.PageResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
            String operatingInfo,
            SpaceSize spaceSize,
            SpaceMood spaceMood,
            String description,
            String price,
            String applicationMethod,
            List<String> facilities,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl,
            boolean liked
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
            String operatingInfo,
            String thumbnail
    ){}

    @Builder
    public record SpaceLikeSummaryResDTO(
            Long spaceId,
            String title,
            String thumbnail,
            String address,
            boolean isLiked
    ){}

    @Builder
    public record MySpaceDetailResDTO(
            Long spaceId,
            String name,
            SpaceVerificationResDTO spaceVerificationResDTO,
            String address,
            String operatingInfo,
            SpaceSize spaceSize,
            SpaceMood spaceMood,
            String description,
            List<String> facilities,
            String phoneNumber,
            String email,
            String websiteUrl,
            String snsUrl,
            List<String> imageUrls
    ){
        @Builder
        public record SpaceVerificationResDTO(
                String bizNumber,
                String businessLicenseFile,
                String buildingRegisterFile
        ){}
    }

    @Builder
    public record SpaceSummaryResDTO(
            Long spaceId,
            String title,
            LocalDateTime createdAt,
            Status status
    ) {}
}