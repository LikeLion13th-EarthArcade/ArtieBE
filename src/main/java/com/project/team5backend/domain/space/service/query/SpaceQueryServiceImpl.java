package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CENTER_LAT = 37.5665;
    private static final double SEOUL_CENTER_LNG = 126.9780;


    //전시 공간 상세 조회
    @Override
    public SpaceResDTO.SpaceDetailResDTO getSpaceDetail(long spaceId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndFacilities(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId);

        return SpaceConverter.toSpaceDetailResDTO(space, imageUrls);
    }

    //전시 검색
    @Override
    public SpaceResDTO.SpaceSearchPageResDTO searchSpace(
            LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size,
            SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        // 동적 쿼리로 전시 검색
        Page<Space> spacePage = spaceRepository.findSpacesWithFilters(
                requestedStartDate, requestedEndDate, district, size, type, mood, facilities, sort, pageable);
        Page<SpaceResDTO.SpaceSearchResDTO> spaceSearchResDTOPage = spacePage
                .map(SpaceConverter::toSpaceSearchResDTO);

        return SpaceConverter.toSpaceSearchPageResDTO(PageResponse.of(spaceSearchResDTOPage), SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }
}