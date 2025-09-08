package com.project.team5backend.domain.space.space.service.query;

import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;
import com.project.team5backend.domain.space.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.space.exception.SpaceException;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.Status;
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
    public SpaceResDTO.DetailSpaceResDTO getSpaceDetail(long spaceId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId);

        return SpaceConverter.toDetailSpaceResDTO(space, imageUrls);
    }

    //전시 검색
    @Override
    public SpaceResDTO.SearchSpacePageResDTO searchSpace(
            LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size,
            SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, int page) {
        // Pageable 생성
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

//        //enum 리스트 -> string 형태로
//        List<String> facilityNames = facilities.stream()
//                .map(Enum::name)
//                .toList();
        // 동적 쿼리로 전시 검색
        Page<Space> spacePage = spaceRepository.findSpacesWithFilters(
                requestedStartDate, requestedEndDate, district, size, type, mood, facilities, sort, pageable);

        // 검색 결과를 DTO로 변환 - Converter 사용
        List<SpaceResDTO.SearchSpaceResDTO> items = spacePage.getContent().stream()
                .map(SpaceConverter::toSearchSpaceResDTO)
                .toList();

        // PageInfo와 MapInfo 생성 - Converter 사용
        return SpaceConverter.toSearchSpacePageResDTO(
                items, spacePage, SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);

    }
//
//    //검색 조건에 맞는 전시 공간 목록 조회
//    @Override
//    public SpaceResponse.SpaceSearchPageResponse searchSpaces(
//            LocalDate startDate,
//            LocalDate endDate,
//            String district,
//            SpaceSize size,
//            SpaceType type,
//            SpaceMood mood,
//            int page
//    ) {
//        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").ascending()); // ✅ id 기준 오름차순 고정
//
//        // Page<Space> 조회
//        Page<Space> spaces = spaceRepository.findSpacesWithFilters(
//                startDate, endDate, district, size, type, mood, pageable
//        );
//
//        // DTO 변환 (static 메서드 사용)
//        List<SpaceResponse.SpaceSearchResponse> content =
//                spaces.getContent().stream()
//                        .map(SpaceConverter::toSearchSpaceResDTO)
//                        .toList();
//
//        // PageInfo 생성
//        SpaceResponse.SpaceSearchPageResponse.PageInfo pageInfo =
//                new SpaceResponse.SpaceSearchPageResponse.PageInfo(
//                        spaces.getNumber(),         // 현재 페이지
//                        spaces.getSize(),           // 페이지 크기
//                        spaces.getTotalElements(),  // 전체 요소 수
//                        spaces.getTotalPages(),     // 전체 페이지 수
//                        spaces.isFirst(),           // 첫 페이지 여부
//                        spaces.isLast()             // 마지막 페이지 여부
//                );
//
//        // MapInfo 생성 (서울 시청 기준 좌표 예시)
//        SpaceResponse.SpaceSearchPageResponse.MapInfo mapInfo =
//                new SpaceResponse.SpaceSearchPageResponse.MapInfo(
//                        37.5665,  // latitude
//                        126.9780  // longitude
//                );
//
//        // 최종 반환
//        return new SpaceResponse.SpaceSearchPageResponse(
//                content,
//                pageInfo,
//                mapInfo
//        );
//    }
}