package com.project.team5backend.domain.exhibition;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionReader {

    private final ExhibitionRepository exhibitionRepository;

    public Exhibition readExhibition(Long exhibitionId) {
        return exhibitionRepository.findByIdAndIsDeletedFalse(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }
}
