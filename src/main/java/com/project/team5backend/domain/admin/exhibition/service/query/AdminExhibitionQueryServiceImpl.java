package com.project.team5backend.domain.admin.exhibition.service.query;

import com.project.team5backend.domain.admin.exhibition.converter.AdminExhibitionConverter;
import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.common.enums.StatusGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminExhibitionQueryServiceImpl implements AdminExhibitionQueryService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;

    private static final int PAGE_SIZE = 10;


    @Override
    public Page<AdminExhibitionResDTO.ExhibitionSummaryResDTO> getSummaryExhibitionList(StatusGroup status, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        Page<Exhibition> exhibitionPage = exhibitionRepository.findAdminExhibitionsByStatus(status, pageable);

        return exhibitionPage.map(AdminExhibitionConverter::toExhibitionSummaryResDTO);
    }

    @Override
    public AdminExhibitionResDTO.ExhibitionDetailResDTO getDetailExhibition(long exhibitionId) {
        Exhibition exhibition = getExhibition(exhibitionId);

        List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();

        return AdminExhibitionConverter.toExhibitionDetailResDTO(exhibition, imageUrls);

    }

    private Exhibition getExhibition(long exhibitionId) {
        return exhibitionRepository.findByIdAndIsDeletedFalse(exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }
}