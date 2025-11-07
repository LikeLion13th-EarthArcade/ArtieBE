package com.project.team5backend.domain.admin.space.service.command;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;

public interface AdminSpaceCommandService {
    AdminSpaceResDTO.SpaceStatusUpdateResDTO approveSpace(Long spaceId);
    AdminSpaceResDTO.SpaceStatusUpdateResDTO rejectSpace(Long spaceId);
}
