package com.project.team5backend.domain.admin.dashboard.dto.response;

import lombok.Builder;

import java.util.List;

public class AdminDashboardResDTO {
    @Builder
    public record AdminDashboardSummaryResDTO(
            long pendingExhibitionCount,
            long pendingSpaceCount,
            long totalExhibitionCount,
            long totalSpaceCount,
            List<ExhibitionSummaryResDTO> pendingExhibitions,
            List<SpaceSummaryResDTO> pendingSpaces
    ) {
    }
}
