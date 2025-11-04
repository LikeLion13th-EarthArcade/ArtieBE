package com.project.team5backend.domain.admin.space.service.query;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;

public interface AdminSpaceQueryService {
    Page<AdminSpaceResDTO.SpaceSummaryResDTO> getSpaceList(StatusGroup status, int page);

    AdminSpaceResDTO.SpaceDetailResDTO getDetailSpace(long spaceId);
}
