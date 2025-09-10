package com.project.team5backend.domain.admin.exhibition.service.command;

import com.project.team5backend.domain.admin.exhibition.converter.AdminExhibitionConverter;
import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminExhibitionCommandServiceImpl implements AdminExhibitionCommandService {

    private final ExhibitionRepository exhibitionRepository;

    @Override
    public AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO approveExhibition(long exhibitionId){
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalse(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        exhibition.approveExhibition();
        return AdminExhibitionConverter.toExhibitionStatusUpdateResDTO(exhibition, "해당 전시가 승인되었습니다.");
    }

    @Override
    public AdminExhibitionResDTO.ExhibitionStatusUpdateResDTO rejectExhibition(long exhibitionId){
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalse(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        exhibition.rejectExhibition();
        return AdminExhibitionConverter.toExhibitionStatusUpdateResDTO(exhibition, "해당 전시가 거절되었습니다.");
    }
}
