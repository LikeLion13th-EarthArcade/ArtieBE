package com.project.team5backend.domain.admin.space.dto.response;

import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.global.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class AdminSpaceResDTO {
    @Builder
    public record SpaceSummaryResDTO(
            Long spaceId,
            String name,
            LocalDateTime createdAt,
            Status status
    ) {}

    @Builder
    public record SpaceStatusUpdateResDTO(
            Long spaceId,
            Status status,
            String message
    ){}

    @Builder
    public record SpaceDetailResDTO(
            Long spaceId,
            String name,
            List<String> imageUrls,
            String address,
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
}
