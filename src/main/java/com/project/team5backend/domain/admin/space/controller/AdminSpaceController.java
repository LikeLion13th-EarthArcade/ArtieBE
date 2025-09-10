package com.project.team5backend.domain.admin.space.controller;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
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


    @Operation(summary = "공간등록 관리 API")
    //@PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public CustomResponse<Page<AdminSpaceResDTO.adminSpaceDetailResDTO>> getAdminSpaceList(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") StatusGroup status,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(adminSpaceQueryService.getAdminSpaceList(status, page));
    }


}
