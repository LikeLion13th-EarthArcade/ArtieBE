package com.project.team5backend.domain.admin.exhibition.service.query;

import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;

public interface AdminExhibitionQueryService {
    Page<AdminExhibitionResDTO.ExhibitionSummaryResDTO> getSummaryExhibitionList(StatusGroup status, int page);

    AdminExhibitionResDTO.ExhibitionDetailResDTO getDetailExhibition(long exhibitionId);
}
