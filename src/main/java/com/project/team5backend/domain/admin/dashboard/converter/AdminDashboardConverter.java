package com.project.team5backend.domain.admin.dashboard.converter;

import com.project.team5backend.domain.admin.dashboard.dto.response.AdminDashboardResDTO;
import com.project.team5backend.domain.admin.dashboard.dto.response.ExhibitionSummaryResDTO;
import com.project.team5backend.domain.admin.dashboard.dto.response.SpaceSummaryResDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdminDashboardConverter {

    public static AdminDashboardResDTO.AdminDashboardSummaryResDTO toSummaryResDTO(
            long pendingExhibitionCount,
            long pendingSpaceCount,
            long totalExhibitionCount,
            long totalSpaceCount,
            List<ExhibitionSummaryResDTO> pendingExhibitions,
            List<SpaceSummaryResDTO> pendingSpaces
    ) {
        return AdminDashboardResDTO.AdminDashboardSummaryResDTO.builder()
                .pendingExhibitionCount(pendingExhibitionCount)
                .pendingSpaceCount(pendingSpaceCount)
                .totalExhibitionCount(totalExhibitionCount)
                .totalSpaceCount(totalSpaceCount)
                .pendingExhibitions(pendingExhibitions)
                .pendingSpaces(pendingSpaces)
                .build();
    }
}
