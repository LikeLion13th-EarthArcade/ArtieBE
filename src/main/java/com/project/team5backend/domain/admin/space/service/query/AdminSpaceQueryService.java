package com.project.team5backend.domain.admin.space.service.query;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.global.entity.enums.StatusGroup;
import org.springframework.data.domain.Page;

public interface AdminSpaceQueryService {
    Page<AdminSpaceResDTO.adminSpaceDetailResDTO> getAdminSpaceList(StatusGroup status, int size);
}
