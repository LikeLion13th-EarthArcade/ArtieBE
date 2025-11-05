package com.project.team5backend.domain.exhibition.service.query;

import com.project.team5backend.domain.common.storage.FileUrlResolverPort;
import com.project.team5backend.domain.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.exception.UserErrorCode;
import com.project.team5backend.domain.user.exception.UserException;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.global.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private static final double SEOUL_CITY_HALL_LAT = 37.5665;
    private static final double SEOUL_CITY_HALL_LNG = 126.9780;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final InteractLogService interactLogService;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final FileUrlResolverPort fileUrlResolverPort;
    private final UserRepository userRepository;

    @Override
    public ExhibitionResDTO.ExhibitionDetailResDTO findExhibitionDetail(Long userId, Long exhibitionId) {
        Exhibition exhibition = getApprovedExhibitionWithDetails(exhibitionId);

        interactLogService.logClick(userId, exhibitionId); // ai 분석을 위한 로그 생성
        List<String> imageUrls = getFileKeys(exhibitionId); // 전시 이미지들의 fileKey만 조회
        boolean liked = exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
        return ExhibitionConverter.toExhibitionDetailResDTO(exhibition, imageUrls, liked);
    }

    @Override
    public ExhibitionResDTO.ExhibitionSearchPageResDTO searchExhibitions(
            ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate, Sort sort, Pageable pageable) {

        Page<Exhibition> exhibitionPage = exhibitionRepository.findExhibitionsWithFilters(exhibitionCategory, district, exhibitionMood, localDate, sort, pageable);
        Page<ExhibitionResDTO.ExhibitionSearchResDTO> exhibitionSearchResDTOPage = getExhibitionSearchResDTOPage(exhibitionPage);

        return ExhibitionConverter.toExhibitionSearchPageResDTO(PageResponse.of(exhibitionSearchResDTOPage), SEOUL_CITY_HALL_LAT, SEOUL_CITY_HALL_LNG);
    }

    @Override
    public List<ExhibitionResDTO.ExhibitionHotNowResDTO> getHotNowExhibitions(Long userId) {
        LocalDate currentDate = LocalDate.now();
        Pageable page = PageRequest.of(0, 4);

        List<Exhibition> exhibitions = exhibitionRepository.findExhibitionHotNow(currentDate, page, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        return exhibitions.stream()
                .map(exhibition -> getExhibitionHotNowResDTO(userId, exhibition))
                .toList();
    }

    @Override
    public ExhibitionResDTO.UpcomingPopularExhibitionResDTO getUpcomingPopularExhibition() {
        LocalDate currentDate = LocalDate.now();
        Pageable topOne = PageRequest.of(0, 1);

        List<Exhibition> exhibitions = exhibitionRepository.findUpcomingPopularExhibition(currentDate, topOne, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        Exhibition upcomingEx = exhibitions.get(0);
        List<String> imageUrls = getFileKeys(upcomingEx.getId());
        return ExhibitionConverter.toUpcomingPopularExhibitionResDTO(upcomingEx.getId(), upcomingEx.getTitle(), imageUrls);
    }

    @Override
    public ExhibitionResDTO.RegionalPopularExhibitionListResDTO getRegionalPopularExhibitions() {
        LocalDate currentDate = LocalDate.now();
        Pageable topFour = PageRequest.of(0, 4);

        List<Exhibition> exhibitions = exhibitionRepository.findTopByDistrict(currentDate, topFour, Status.APPROVED);
        if (exhibitions.size() < 4) {
            throw new ExhibitionException(
                    exhibitions.isEmpty()
                            ? ExhibitionErrorCode.EXHIBITION_NOT_FOUND
                            : ExhibitionErrorCode.DIFFERENT_EXHIBITION_NOT_FOUND
            );
        }
        return ExhibitionConverter.toRegionalPopularExhibitionListResDTO(
                exhibitions.stream()
                        .map(this::getRegionalPopularExhibitionResDTO)
                        .toList()
        );
    }

    @Override
    public List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtieRecommendations(Long userId) {
        LocalDate today = LocalDate.now();

        List<Exhibition> candidates = exhibitionRepository.findUnpopularCandidates(today, 20); // 후보 20개
        Collections.shuffle(candidates, new Random(today.toEpochDay())); // 하루 단위 고정 셔플

        return candidates.stream()
                .limit(4)
                .map(exhibition -> getArtieRecommendationResDTO(userId, exhibition))
                .toList();
    }

    @Override
    public Page<ExhibitionResDTO.ExhibitionSummaryResDTO> getSummaryExhibitionList(Long userId, StatusGroup status, Sort sort, Pageable pageable) {
        Page<Exhibition> exhibitionPage = exhibitionRepository.findMyExhibitionsByStatus(userId, status, sort, pageable);
        return exhibitionPage.map(ExhibitionConverter::toExhibitionSummaryResDTO);
    }

    @Override
    public ExhibitionResDTO.MyExhibitionDetailResDTO getMyDetailExhibition(Long userId, Long exhibitionId) {
        Exhibition exhibition = getOwnedActiveExhibition(userId, exhibitionId);
        List<String> imageUrls = getFileKeys(exhibitionId);
        return ExhibitionConverter.toMyExhibitionDetailResDTO(exhibition, imageUrls);
    }

    @Override
    public Page<ExhibitionResDTO.ExhibitionLikeSummaryResDTO> getInterestedExhibitions(Long userId, Pageable pageable) {
        User user = getActiveUser(userId);

        List<Long> interestedExhibitionIds = exhibitionLikeRepository.findExhibitionIdsByInterestedUser(user);
        Page<Exhibition> interestedExhibitions = exhibitionRepository.findByIdIn(interestedExhibitionIds, pageable);

        return interestedExhibitions.map(exhibition -> getExhibitionLikeSummaryResDTO(exhibition, interestedExhibitionIds, LocalDate.now()));
    }

    private ExhibitionResDTO.ExhibitionLikeSummaryResDTO getExhibitionLikeSummaryResDTO(Exhibition exhibition, List<Long> interestedExhibitionIds, LocalDate today) {
        String thumbnail = fileUrlResolverPort.toFileUrl(exhibition.getThumbnail());
        boolean isLiked = interestedExhibitionIds.contains(exhibition.getId());
        boolean opening = isOpening(exhibition, today);
        return ExhibitionConverter.toExhibitionLikeSummaryResDTO(exhibition, thumbnail, isLiked, opening);
    }

    private User getActiveUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private boolean isOpening(Exhibition exhibition, LocalDate today) {
        return exhibition.getStartDate() != null && exhibition.getEndDate() != null
                && !today.isBefore(exhibition.getStartDate())
                && !today.isAfter(exhibition.getEndDate());
    }

    private List<String> getFileKeys(Long exhibitionId){
        return exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId).stream()
                .map(fileUrlResolverPort::toFileUrl)
                .toList();
    }

    private Exhibition getApprovedExhibitionWithDetails(Long exhibitionId){
        return exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndExhibitionFacilities(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }

    private Page<ExhibitionResDTO.ExhibitionSearchResDTO> getExhibitionSearchResDTOPage(Page<Exhibition> exhibitionPage) {
        return exhibitionPage
                .map(exhibition -> {
                    String thumbnail = fileUrlResolverPort.toFileUrl(exhibition.getThumbnail());
                    return ExhibitionConverter.toExhibitionSearchResDTO(exhibition, thumbnail);
                });
    }

    private ExhibitionResDTO.ExhibitionHotNowResDTO getExhibitionHotNowResDTO(Long userId, Exhibition exhibition) {
        String thumbnail = fileUrlResolverPort.toFileUrl(exhibition.getThumbnail());
        boolean liked = isExhibitionLiked(userId, exhibition.getId());
        return ExhibitionConverter.toExhibitionHotNowResDTO(exhibition, liked, thumbnail);
    }


    private ExhibitionResDTO.RegionalPopularExhibitionResDTO getRegionalPopularExhibitionResDTO(Exhibition exhibition) {
        String thumbnail = fileUrlResolverPort.toFileUrl(exhibition.getThumbnail());
        return ExhibitionConverter.toRegionalPopularExhibitionResDTO(exhibition, thumbnail);
    }

    private ExhibitionResDTO.ArtieRecommendationResDTO getArtieRecommendationResDTO(Long userId, Exhibition exhibition) {
        String thumbnail = fileUrlResolverPort.toFileUrl(exhibition.getThumbnail());
        boolean liked = isExhibitionLiked(userId, exhibition.getId());
        return ExhibitionConverter.toArtieRecommendationResDTO(exhibition, liked, thumbnail);
    }

    private Exhibition getOwnedActiveExhibition(Long userId, Long exhibitionId) {
        return exhibitionRepository.findByIdAndUserIdAndIsDeletedFalse(userId, exhibitionId)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
    }

    private boolean isExhibitionLiked(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }
}
