package com.project.team5backend.domain.admin.dashboard.controller;

import com.project.team5backend.domain.admin.dashboard.dto.response.AdminDashboardResDTO;
import com.project.team5backend.domain.admin.dashboard.service.query.AdminDashboardQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ap1/v1/admin/dashboards")
@Tag(name = "Admin Dashboard", description = "admin 대시보드 관련 API")
public class AdminDashboardController {

    private final AdminDashboardQueryService adminDashBoardQueryService;

    @Operation(summary = "대시보드")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public CustomResponse<AdminDashboardResDTO.AdminDashboardSummaryResDTO> getAdminDashBoard() {
        return CustomResponse.onSuccess(adminDashBoardQueryService.getAdminDashboard());
    }
}
