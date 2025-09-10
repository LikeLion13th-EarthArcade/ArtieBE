package com.project.team5backend.domain.admin.exhibition.service.command;

import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;

public interface AdminExhibitionCommandService {
    AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO approveExhibition(long exhibitionId);
    AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO rejectExhibition(long exhibitionId);
}
