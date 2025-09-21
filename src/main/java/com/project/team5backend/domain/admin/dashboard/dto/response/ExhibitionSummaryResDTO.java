package com.project.team5backend.domain.admin.dashboard.dto.response;

import java.time.LocalDateTime;

public record ExhibitionSummaryResDTO(
        String title,
        String registrant,
        String location,
        LocalDateTime createdAt
) {}