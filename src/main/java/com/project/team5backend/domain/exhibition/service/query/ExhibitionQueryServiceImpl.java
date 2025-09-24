package com.project.team5backend.domain.exhibition.service.query;

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
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.Status;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CITY_HALL_LAT = 37.5665;
    private static final double SEOUL_CITY_HALL_LNG = 126.9780;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final InteractLogService interactLogService;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final S3UrlResolver s3UrlResolver;
    private final UserRepository userRepository;

    @Override
    public ExhibitionResDTO.ExhibitionDetailResDTO findExhibitionDetail(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndExhibitionFacilities(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // ai 분석을 위한 로그 생성
        interactLogService.logClick(1L, exhibitionId);
        // 전시 이미지들의 fileKey만 조회
        List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId).stream()
                .map(s3UrlResolver::toFileUrl)
                .toList();

        return ExhibitionConverter.toExhibitionDetailResDTO(exhibition, imageUrls);
    }

    @Override
    public ExhibitionResDTO.ExhibitionSearchPageResDTO searchExhibitions(
            ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate, Sort sort, int page) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        // 동적 쿼리로 전시 검색
        Page<Exhibition> exhibitionPage = exhibitionRepository.findExhibitionsWithFilters(
                exhibitionCategory, district, exhibitionMood, localDate, sort, pageable);
        Page<ExhibitionResDTO.ExhibitionSearchResDTO> exhibitionSearchResDTOPage = exhibitionPage
                .map(exhibition -> {
                    String thumbnail = s3UrlResolver.toFileUrl(exhibition.getThumbnail());
                    return ExhibitionConverter.toExhibitionSearchResDTO(exhibition, thumbnail);
                });

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
                .map(exhibition ->
                {
                    String thumbnail = s3UrlResolver.toFileUrl(exhibition.getThumbnail());
                    boolean liked = isExhibitionLiked(userId, exhibition.getId());
                    return ExhibitionConverter.toExhibitionHotNowResDTO(exhibition, liked, thumbnail);
                })
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
        List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(upcomingEx.getId()).stream()
                .map(s3UrlResolver::toFileUrl)
                .toList();
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
                        .map(exhibition -> {
                            String thumbnail = s3UrlResolver.toFileUrl(exhibition.getThumbnail());
                            return ExhibitionConverter.toRegionalPopularExhibitionResDTO(exhibition, thumbnail);
                        })
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
                .map(exhibition ->
                {
                    String thumbnail = s3UrlResolver.toFileUrl(exhibition.getThumbnail());
                    boolean liked = isExhibitionLiked(userId, exhibition.getId());
                    return ExhibitionConverter.toArtieRecommendationResDTO(exhibition, liked, thumbnail);
                })
                .toList();
    }

    private boolean isExhibitionLiked(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }

    @Override
    public Page<ExhibitionResDTO.ExhibitionDetailResDTO> getInterestedExhibitions(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        List<Long> interestedExhibitionIds = exhibitionLikeRepository.findExhibitionIdsByInterestedUser(user);

        Page<Exhibition> interestedExhibitions = exhibitionRepository.findByIdIn(interestedExhibitionIds, pageable);

        return interestedExhibitions.map(exhibition -> {
            List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(exhibition.getId())
                    .stream()
                    .map(s3UrlResolver::toFileUrl)
                    .toList();

            return ExhibitionConverter.toExhibitionDetailResDTO(exhibition, imageUrls);
        });
    }
}
