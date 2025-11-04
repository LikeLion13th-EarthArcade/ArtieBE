package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.repository.SpaceImageRepository;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final FileUrlResolverPort fileUrlResolverPort;
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
                .map(fileUrlResolverPort::toFileUrl)
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
                    String thumbnail = fileUrlResolverPort.toFileUrl(space.getThumbnail());
                    return SpaceConverter.toSpaceSearchResDTO(space, thumbnail);
                });

        return SpaceConverter.toSpaceSearchPageResDTO(PageResponse.of(spaceSearchResDTOPage), SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }

    @Override
    public Page<SpaceResDTO.SpaceLikeSummaryResDTO> getInterestedSpaces(long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        List<Long> interestedSpaceIds = spaceLikeRepository.findSpaceIdsByInterestedUser(user);

        Page<Space> interestedSpaces = spaceRepository.findByIdIn(interestedSpaceIds, pageable);

        return interestedSpaces.map(space -> {
            String thumbnail = fileUrlResolverPort.toFileUrl(space.getThumbnail());
            boolean isLiked = interestedSpaceIds.contains(space.getId());
            return SpaceConverter.toSpaceLikeSummaryResDTO(space, thumbnail, isLiked);
        });
    }

    public Page<SpaceResDTO.SpaceDetailResDTO> getMySpace(long userId, StatusGroup statusGroup, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Page<Space> spacePage = spaceRepository.findByUserWithFilters(user, statusGroup, pageable);

        return spacePage.map(space -> {
            List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(space.getId())
                    .stream()
                    .map(fileUrlResolverPort::toFileUrl)
                    .toList();

            return SpaceConverter.toSpaceDetailResDTO(space, imageUrls);
        });
    }

    @Override
    public SpaceResDTO.MySpaceDetailResDTO getMySpaceDetail(long userId, long spaceId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Space space = spaceRepository.findByIdAndIsDeletedFalse(spaceId)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.SPACE_NOT_FOUND));

        if (!Objects.equals(space.getUser(), user)) {
            throw new SpaceException(SpaceErrorCode.SPACE_FORBIDDEN);
        }

        List<String> imageUrls = spaceImageRepository.findImageUrlsBySpaceId(spaceId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();

        SpaceVerification spaceVerification = space.getSpaceVerification();
        String businessLicenseFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBusinessLicenseKey());
        String buildingRegisterFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBuildingRegisterKey());

        return SpaceConverter.toMySpaceDetailResDTO(space, spaceVerification, imageUrls, businessLicenseFile, buildingRegisterFile);
    }
}