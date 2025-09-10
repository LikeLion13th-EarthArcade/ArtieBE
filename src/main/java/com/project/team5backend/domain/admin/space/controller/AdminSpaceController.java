package com.project.team5backend.domain.admin.space.controller;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.admin.space.service.command.AdminSpaceCommandService;
import com.project.team5backend.domain.admin.space.service.query.AdminSpaceQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.entity.enums.StatusGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/spaces")
@Tag(name = "Admin Space", description = "admin 공간 관련 API")
public class AdminSpaceController {

    private final AdminSpaceQueryService adminSpaceQueryService;
    private final AdminSpaceCommandService adminSpaceCommandService;

    @Operation(summary = "공간 승인", description = "status가 pending인 공간 approve로 변경")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{spaceId}/approve")
    public CustomResponse<AdminSpaceResDTO.SpaceStatusUpdateResDTO> approveSpace (@PathVariable Long spaceId){
        return CustomResponse.onSuccess(adminSpaceCommandService.approveSpace(spaceId));
    }
    @Operation(summary = "공간 거절", description = "status가 pending인 공간 reject로 변경")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{spaceId}/reject")
    public CustomResponse<AdminSpaceResDTO.SpaceStatusUpdateResDTO> rejectSpace (@PathVariable Long spaceId){
        return CustomResponse.onSuccess(adminSpaceCommandService.rejectSpace(spaceId));
    }

    @Operation(summary = "공간등록 관리 ")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public CustomResponse<Page<AdminSpaceResDTO.SpaceSummaryResDTO>> getAdminSpaceList(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") StatusGroup status,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(adminSpaceQueryService.getSummarySpaceList(status, page));
    }

    @Operation(summary = "공간 상세 보기 (admin)", description = "상태에 상관없이 공간 상세 보기 가능")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{spaceId}")
    public CustomResponse<AdminSpaceResDTO.SpaceDetailResDTO> getDetailSpace(@PathVariable Long spaceId) {
        return CustomResponse.onSuccess(adminSpaceQueryService.getDetailSpace(spaceId));
    }

}
