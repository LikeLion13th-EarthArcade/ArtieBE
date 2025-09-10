package com.project.team5backend.domain.admin.space.dto.response;

import com.project.team5backend.global.entity.enums.Status;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class AdminSpaceResDTO {
    @Builder
    public record adminSpaceDetailResDTO(
            Long spaceId,
            String name,
            LocalDateTime createdAt,
            Status status
    ) {}
}
