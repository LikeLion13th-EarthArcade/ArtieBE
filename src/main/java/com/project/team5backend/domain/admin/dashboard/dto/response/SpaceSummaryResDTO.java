package com.project.team5backend.domain.admin.dashboard.dto.response;

import java.time.LocalDateTime;

public record SpaceSummaryResDTO(
        String name,
        String registrant,
        String location,
        LocalDateTime createdAt
) {}
