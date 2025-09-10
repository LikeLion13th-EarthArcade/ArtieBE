package com.project.team5backend.domain.admin.space.service.query;

import com.project.team5backend.domain.admin.space.converter.AdminSpaceConverter;
import com.project.team5backend.domain.admin.space.dto.response.AdminSpaceResDTO;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.StatusGroup;
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
public class AdminSpaceQueryServiceImpl implements AdminSpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;

    private static final int PAGE_SIZE = 10;

    @Override
    public Page<AdminSpaceResDTO.SpaceSummaryResDTO> getSummarySpaceList(StatusGroup status, int page){
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        Page<Space> spacePage = spaceRepository.findAdminSpacesByStatus(status, pageable);

        return spacePage.map(AdminSpaceConverter::toSpaceSummaryResDTO);
    }

    @Override
    public AdminSpaceResDTO.SpaceDetailResDTO getDetailSpace(long spaceId){
        Space space = spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId);

        return AdminSpaceConverter.toSpaceDetailResDTO(space, imageUrls);
    }
}
