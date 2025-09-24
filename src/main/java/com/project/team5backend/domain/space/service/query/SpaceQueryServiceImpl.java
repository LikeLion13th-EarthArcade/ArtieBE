package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.global.entity.enums.StatusGroup;
import com.project.team5backend.global.util.PageResponse;
import com.project.team5backend.global.util.S3UrlResolver;
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
    private final S3UrlResolver s3UrlResolver;
    private final UserRepository userRepository;
    private final SpaceLikeRepository spaceLikeRepository;

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CENTER_LAT = 37.5665;
    private static final double SEOUL_CENTER_LNG = 126.9780;


    //전시 공간 상세 조회
    @Override
    public SpaceResDTO.SpaceDetailResDTO getSpaceDetail(long spaceId) {
        Space space = spaceRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndFacilities(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId).stream()
                .map(s3UrlResolver::toFileUrl)
                .toList();

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
                .map(space -> {
                    String thumbnail = s3UrlResolver.toFileUrl(space.getThumbnail());
                    return SpaceConverter.toSpaceSearchResDTO(space, thumbnail);
                });

        return SpaceConverter.toSpaceSearchPageResDTO(PageResponse.of(spaceSearchResDTOPage), SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }

    @Override
    public Page<SpaceResDTO.SpaceDetailResDTO> getInterestedSpaces(long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        List<Long> interestedSpaceIds = spaceLikeRepository.findSpaceIdsByInterestedUser(user);

        Page<Space> interestedSpaces = spaceRepository.findByIdIn(interestedSpaceIds, pageable);

        return interestedSpaces.map(space -> {
            List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(space.getId())
                    .stream()
                    .map(s3UrlResolver::toFileUrl)
                    .toList();

            return SpaceConverter.toSpaceDetailResDTO(space, imageUrls);
        });
    }

    public Page<SpaceResDTO.SpaceDetailResDTO> getMySpace(long userId, StatusGroup statusGroup, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<Space> spacePage = spaceRepository.findByUserWithFilters(user, statusGroup, pageable);

        return spacePage.map(space -> {
            List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(space.getId())
                    .stream()
                    .map(s3UrlResolver::toFileUrl)
                    .toList();

            return SpaceConverter.toSpaceDetailResDTO(space, imageUrls);
        });
    }
}