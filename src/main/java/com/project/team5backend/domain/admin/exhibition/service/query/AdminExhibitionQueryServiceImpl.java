package com.project.team5backend.domain.admin.exhibition.service.query;

import com.project.team5backend.domain.admin.exhibition.converter.AdminExhibitionConverter;
import com.project.team5backend.domain.admin.exhibition.dto.response.AdminExhibitionResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.domain.exhibition.ExhibitionReader;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.ExhibitionImageReader;
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
    private final ExhibitionReader exhibitionReader;
    private final ExhibitionImageReader exhibitionImageReader;

    private static final int PAGE_SIZE = 10;


    @Override
    public Page<AdminExhibitionResDTO.ExhibitionSummaryResDTO> getSummaryExhibitionList(StatusGroup status, int page) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        Page<Exhibition> exhibitionPage = exhibitionRepository.findAdminExhibitionsByStatus(status, pageable);

        return exhibitionPage.map(AdminExhibitionConverter::toExhibitionSummaryResDTO);
    }

    @Override
    public AdminExhibitionResDTO.ExhibitionDetailResDTO getDetailExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionReader.readExhibition(exhibitionId);
        List<String> imageUrls = exhibitionImageReader.getExhibitionImageUrls(exhibitionId);
        return AdminExhibitionConverter.toExhibitionDetailResDTO(exhibition, imageUrls);
    }
}