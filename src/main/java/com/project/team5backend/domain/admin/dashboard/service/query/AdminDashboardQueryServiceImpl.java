package com.project.team5backend.domain.admin.dashboard.service.query;

import com.project.team5backend.domain.admin.dashboard.converter.AdminDashboardConverter;
import com.project.team5backend.domain.admin.dashboard.dto.response.AdminDashboardResDTO;
import com.project.team5backend.domain.admin.dashboard.dto.response.ExhibitionSummaryResDTO;
import com.project.team5backend.domain.admin.dashboard.dto.response.SpaceSummaryResDTO;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.common.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDashboardQueryServiceImpl implements AdminDashboardQueryService {

    private final ExhibitionRepository exhibitionRepository;
    private final SpaceRepository spaceRepository;
    @Override
    public AdminDashboardResDTO.AdminDashboardSummaryResDTO getAdminDashboard() {
        long pendingExhibitionCount = exhibitionRepository.findPendingExhibitionsCountByStatus(Status.PENDING);
        long pendingSpaceCount = spaceRepository.findPendingSpacesCountByStatus(Status.PENDING);
        long totalExhibitionCount = exhibitionRepository.count();
        long totalSpaceCount = spaceRepository.count();
        List<ExhibitionSummaryResDTO> pendingExhibitions = exhibitionRepository.findTop3ByStatus(Status.PENDING);
        List<SpaceSummaryResDTO> pendingSpaces = spaceRepository.findTop3ByStatus(Status.PENDING);

        return AdminDashboardConverter.toSummaryResDTO(
                pendingExhibitionCount,
                pendingSpaceCount,
                totalExhibitionCount,
                totalSpaceCount,
                pendingExhibitions,
                pendingSpaces
        );
    }
}
