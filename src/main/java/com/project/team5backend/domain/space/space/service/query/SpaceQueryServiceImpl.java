package com.project.team5backend.domain.space.space.service.query;


import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.space.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.space.exception.SpaceException;
import com.project.team5backend.domain.space.space.repository.SpaceRepository;
import com.project.team5backend.global.entity.enums.Status;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;

    //전시 공간 상세 조회
    @Override
    public SpaceResDTO.DetailSpaceResDTO getSpaceDetail(long spaceId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApproved(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId);

        return SpaceConverter.toDetailSpaceResDTO(space, imageUrls);
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