package com.project.team5backend.domain.admin.space.service.command;

import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;

public interface AdminSpaceCommandService {
    AdminSpaceResDTO.SpaceStatusUpdateResDTO approveSpace(long spaceId);
    AdminSpaceResDTO.SpaceStatusUpdateResDTO rejectSpace(long spaceId);
}
