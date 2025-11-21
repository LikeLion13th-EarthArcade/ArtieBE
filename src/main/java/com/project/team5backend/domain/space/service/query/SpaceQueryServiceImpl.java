package com.project.team5backend.domain.space.service.query;

import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.image.SpaceImageReader;
import com.project.team5backend.domain.reservation.ReservationReader;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import com.project.team5backend.domain.space.SpaceLikeReader;
import com.project.team5backend.domain.space.SpaceReader;
import com.project.team5backend.domain.space.converter.SpaceConverter;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.ClosedDay;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.space.entity.SpaceVerification;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.space.exception.SpaceErrorCode;
import com.project.team5backend.domain.space.exception.SpaceException;
import com.project.team5backend.domain.space.repository.ClosedDayRepository;
import com.project.team5backend.domain.space.repository.SpaceLikeRepository;
import com.project.team5backend.domain.space.repository.SpaceRepository;
import com.project.team5backend.domain.user.UserReader;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.util.PageResponse;
import com.project.team5backend.global.util.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.project.team5backend.domain.common.util.DateUtils.generateSlots;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SpaceQueryServiceImpl implements SpaceQueryService {

    private final SpaceRepository spaceRepository;
    private final SpaceImageReader spaceImageReader;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final SpaceLikeRepository spaceLikeRepository;
    private final SpaceLikeReader spaceLikeReader;
    private final SpaceReader spaceReader;
    private final UserReader userReader;
    private final ReservationRepository reservationRepository;
    private final RedisUtils<String> redisUtils;

    private static final double SEOUL_CENTER_LAT = 37.5665;
    private static final double SEOUL_CENTER_LNG = 126.9780;
    private final ReservationReader reservationReader;


    //전시 공간 상세 조회
    @Override
    public SpaceResDTO.SpaceDetailResDTO getSpaceDetail(Long userId, Long spaceId) {
        Space space = getApprovedSpaceWithDetails(spaceId);
        List<String> imageUrls = spaceImageReader.readSpaceImageUrls(spaceId);
        boolean liked = spaceLikeReader.isLikedByUser(userId, spaceId);
        return SpaceConverter.toSpaceDetailResDTO(space, imageUrls, liked);
    }

    //전시 검색
    @Override
    public SpaceResDTO.SpaceSearchPageResDTO searchSpace(
            LocalDate requestedStartDate, LocalDate requestedEndDate, String district, SpaceSize size,
            SpaceType type, SpaceMood mood, List<String> facilities, Sort sort, Pageable pageable) {

        Page<Space> spacePage = spaceRepository.findSpacesWithFilters(requestedStartDate, requestedEndDate, district, size, type, mood, facilities, sort, pageable);
        Page<SpaceResDTO.SpaceSearchResDTO> spaceSearchResDTOPage = getSpaceSearchResDTOPage(spacePage);

        return SpaceConverter.toSpaceSearchPageResDTO(PageResponse.of(spaceSearchResDTOPage), SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }

    @Override
    public Page<SpaceResDTO.SpaceLikeSummaryResDTO> getInterestedSpaces(Long userId, Pageable pageable) {
        User user = userReader.readUser(userId);

        List<Long> interestedSpaceIds = spaceLikeRepository.findSpaceIdsByInterestedUser(user);
        Page<Space> interestedSpaces = spaceRepository.findByIdIn(interestedSpaceIds, pageable);

        return interestedSpaces.map(space -> getSpaceLikeSummaryResDTO(space, interestedSpaceIds));
    }

    @Override
    public Page<SpaceResDTO.SpaceSummaryResDTO> getMySpaces(Long userId, StatusGroup status, Sort sort, Pageable pageable) {
        Page<Space> spacePage = spaceRepository.findMySpacesByStatus(userId, status, sort, pageable);
        return spacePage.map(SpaceConverter::toSpaceSummaryResDTO);
    }

    @Override
    public SpaceResDTO.MySpaceDetailResDTO getMySpaceDetail(Long userId, Long spaceId) {
        Space space = spaceReader.readSpace(spaceId);
        List<String> imageUrls = spaceImageReader.readSpaceImageUrls(spaceId);

        SpaceVerification spaceVerification = space.getSpaceVerification();
        String businessLicenseFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBusinessLicenseKey());
        String buildingRegisterFile = fileUrlResolverPort.toFileUrl(spaceVerification.getBuildingRegisterKey());
        return SpaceConverter.toMySpaceDetailResDTO(space, spaceVerification, imageUrls, businessLicenseFile, buildingRegisterFile);
    }

    @Override
    public SpaceResDTO.SpaceAvailabilityResDTO getAvailability(Long spaceId, LocalDate startDate, LocalDate endDate) {
//        List<ClosedDay> closedDays = closedDayRepository.findBySpaceId(spaceId);
        List<LocalDate> dateSlots = generateSlots(startDate, endDate);

        List<LocalDate> availableDates = new ArrayList<>();
        List<LocalDate> unavailableDates = new ArrayList<>();
        boolean isAvailable = true;

        for (LocalDate date : dateSlots) {
            // 현재 요청 날짜가 어떤 휴무 규칙에 위배되는 순간 false
//            boolean isClosed = closedDays.stream()
//                    .anyMatch(cd -> cd.isClosedOn(date));
            boolean isReserved = reservationRepository.existsByDateAndTimeSlots(date);
            boolean isLocked = redisUtils.hasKey("system:lock:" + spaceId + ":" + date);
//            if (isClosed || isReserved) {
            if (isReserved || isLocked) {
                unavailableDates.add(date);
                isAvailable = false;
            } else {
                availableDates.add(date);
            }
        }
        return SpaceConverter.toSpaceAvailabilityResDTO(spaceId, availableDates, unavailableDates, isAvailable);
    }

    private Space getApprovedSpaceWithDetails(Long spaceId) {
        return spaceRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndFacilities(spaceId, Status.APPROVED)
                .orElseThrow(() -> new SpaceException(SpaceErrorCode.APPROVED_SPACE_NOT_FOUND));
    }

    private SpaceResDTO.SpaceLikeSummaryResDTO getSpaceLikeSummaryResDTO(Space space, List<Long> interestedSpaceIds) {
        String thumbnail = fileUrlResolverPort.toFileUrl(space.getThumbnail());
        boolean isLiked = interestedSpaceIds.contains(space.getId());
        return SpaceConverter.toSpaceLikeSummaryResDTO(space, thumbnail, isLiked);
    }

    private Page<SpaceResDTO.SpaceSearchResDTO> getSpaceSearchResDTOPage(Page<Space> spacePage) {
        return spacePage
                .map(space -> {
                    String thumbnail = fileUrlResolverPort.toFileUrl(space.getThumbnail());
                    return SpaceConverter.toSpaceSearchResDTO(space, thumbnail);
                });
    }

}
