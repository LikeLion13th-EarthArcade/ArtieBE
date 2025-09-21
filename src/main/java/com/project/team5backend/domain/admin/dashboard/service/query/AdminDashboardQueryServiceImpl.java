package com.project.team5backend.domain.admin.dashboard.service.query;

import com.project.team5backend.domain.admin.dashboard.dto.response.AdminDashboardResDTO;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminDashboardQueryServiceImpl implements AdminDashboardQueryService {

    private final ExhibitionRepository exhibitionRepository;
    private final SpaceRepository spaceRepository;
    @Override
    public AdminDashboardResDTO.AdminDashboardSummaryResDTO getAdminDashboard() {
        return AdminDashboardResDTO.AdminDashboardSummaryResDTO.builder()
                .pendingExhibitionCount(exhibitionRepository.findPendingExhibitionsCountByStatus(Status.PENDING))
                .pendingSpaceCount(spaceRepository.findPendingSpacesCountByStatus(Status.PENDING))
                .totalExhibitionCount(exhibitionRepository.count())
                .totalSpaceCount(spaceRepository.count())
                .pendingExhibitions(exhibitionRepository.findTop3ByStatus(Status.PENDING))
                .pendingSpaces(spaceRepository.findTop3ByStatus(Status.PENDING))
                .build();
    }
}
