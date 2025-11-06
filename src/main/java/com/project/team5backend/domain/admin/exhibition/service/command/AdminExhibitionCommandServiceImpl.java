package com.project.team5backend.domain.admin.exhibition.service.command;

import com.project.team5backend.domain.admin.exhibition.converter.AdminExhibitionConverter;
import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminExhibitionCommandServiceImpl implements AdminExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;

    @Override
    public AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO approveExhibition(Long exhibitionId){
        Exhibition exhibition = getExhibition(exhibitionId);

        exhibition.approveExhibition();
        return AdminExhibitionConverter.toExhibitionStatusUpdateResDTO(exhibition, "해당 전시가 승인되었습니다.");
    }

    @Override
    public AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO rejectExhibition(Long exhibitionId){
        Exhibition exhibition = getExhibition(exhibitionId);

        exhibition.rejectExhibition();
        return AdminExhibitionConverter.toExhibitionStatusUpdateResDTO(exhibition, "해당 전시가 거절되었습니다.");
    }

    private Exhibition getExhibition(Long exhibitionId){
        return exhibitionRepository.findByIdAndIsDeletedFalse(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }
}
