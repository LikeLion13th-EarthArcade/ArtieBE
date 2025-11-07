package com.project.team5backend.domain.admin.exhibition.service.command;

import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;

public interface AdminExhibitionCommandService {
    AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO approveExhibition(Long exhibitionId);
    AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO rejectExhibition(Long exhibitionId);
}
