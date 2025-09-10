package com.project.team5backend.domain.admin.dashboard.service.query;

import com.project.team5backend.domain.admin.dashboard.dto.response.AdminDashboardResDTO;

public interface AdminDashboardQueryService {
    AdminDashboardResDTO.AdminDashboardSummaryResDTO getAdminDashboard();
}
