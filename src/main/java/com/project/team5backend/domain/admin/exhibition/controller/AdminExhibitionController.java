package com.project.team5backend.domain.admin.exhibition.controller;

import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.admin.exhibition.service.command.AdminExhibitionCommandService;
import com.project.team5backend.domain.admin.exhibition.service.query.AdminExhibitionQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.entity.enums.StatusGroup;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/exhibitions")
@Tag(name = "Admin Exhibition", description = "admin 전시 관련 API")
public class AdminExhibitionController {

    private final AdminExhibitionQueryService adminExhibitionQueryService;
    private final AdminExhibitionCommandService adminExhibitionCommandService;

    @Operation(summary = "전시 승인", description = "status가 pending인 전시 approve로 변경")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{exhibitionId}/approve")
    public CustomResponse<AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO> approveExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(adminExhibitionCommandService.approveExhibition(exhibitionId));
    }

    @Operation(summary = "전시 거절", description = "status가 pending인 전시 reject로 변경")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{exhibitionId}/reject")
    public CustomResponse<AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO> rejectExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(adminExhibitionCommandService.rejectExhibition(exhibitionId));
    }

    @Operation(summary = "전시등록 관리 ",
            description = "현재 서비스에서 생성된 모든 전시를 검색합니다. <br> " +
                    "[RequestParam statusGroup] : [ALL], [PENDING], [DONE] 3가지 선택<br><br>" +
                    "ALL : 모든 상태의 전시 <br><br>" +
                    "PENDING : 대기중 <br>PENDING(호스트의 확정 대기) <br><br>" +
                    "DONE : 완료됨 <br>APPROVED(호스트의 전시 승인 확정), REJECTED(호스트의 전시 거절 확정), <br>")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public CustomResponse<PageResponse<AdminExhibitionResDTO.ExhibitionSummaryResDTO>> getExhibitionList(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") StatusGroup status,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(PageResponse.of(adminExhibitionQueryService.getSummaryExhibitionList(status, page)));
    }

    @Operation(summary = "전시 상세 보기 (admin)", description = "상태에 상관없이 전시 상세 보기 가능")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{exhibitionId}")
    public CustomResponse<AdminExhibitionResDTO.ExhibitionDetailResDTO> getDetailExhibition(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(adminExhibitionQueryService.getDetailExhibition(exhibitionId));
    }
}