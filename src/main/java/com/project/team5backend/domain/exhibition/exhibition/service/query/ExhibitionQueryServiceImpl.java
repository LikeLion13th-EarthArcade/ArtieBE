package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.converter.ExhibitionConverter;
import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionErrorCode;
import com.project.team5backend.domain.exhibition.exhibition.exception.ExhibitionException;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionLikeRepository;
import com.project.team5backend.domain.exhibition.exhibition.repository.ExhibitionRepository;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.domain.exhibition.review.converter.ExhibitionReviewConverter;
import com.project.team5backend.domain.exhibition.review.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.exhibition.review.repository.ExhibitionReviewRepository;
import com.project.team5backend.domain.image.repository.ExhibitionImageRepository;
import com.project.team5backend.domain.image.repository.ExhibitionReviewImageRepository;
import com.project.team5backend.domain.recommendation.service.InteractLogService;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.user.repository.UserRepository;
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
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CENTER_LAT = 37.5665;
    private static final double SEOUL_CENTER_LNG = 126.9780;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final InteractLogService interactLogService;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final UserRepository userRepository;
    private final ExhibitionReviewRepository exhibitionReviewRepository;
    private final ExhibitionReviewImageRepository exhibitionReviewImageRepository;
    @Override
    public ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprove(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // ai 분석을 위한 로그 생성
        interactLogService.logClick(1L, exhibitionId);
        // 전시 이미지들의 fileKey만 조회
        List<String> imageFileKeys = exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId);

        List<ExhibitionReview> reviews = exhibitionReviewRepository.findAllByExhibitionId(exhibition.getId());

        List<ExhibitionReviewResDTO.exReviewDetailResDTO> detailReviews = reviews.stream()
                .map(review -> {
                    // 리뷰에 연결된 이미지 fileKey 조회
                    List<String> imageUrls = exhibitionReviewImageRepository.findImageUrlsByExhibitionReviewId(review.getId());
                    return ExhibitionReviewConverter.toDetailExReviewResDTO(review, imageUrls);
                })
                .toList();

        return ExhibitionConverter.toDetailExhibitionResDTO(exhibition, imageFileKeys, detailReviews);
    }

    @Override
    public ExhibitionResDTO.SearchExhibitionPageResDTO searchExhibition(
            ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate, Sort sort, int page) {

        log.info("전시 검색 - exhibitionCategory: {}, district: {}, exhibitionMood: {}, localDate: {}, sort: {}, page: {}",
                exhibitionCategory, district, exhibitionMood, localDate, sort, page);

        // Pageable 생성
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        // 동적 쿼리로 전시 검색
        Page<Exhibition> exhibitionPage = exhibitionRepository.findExhibitionsWithFilters(
                exhibitionCategory, district, exhibitionMood, localDate, sort, pageable);

        // 검색 결과를 DTO로 변환 - Converter 사용
        List<ExhibitionResDTO.SearchExhibitionResDTO> items = exhibitionPage.getContent().stream()
                .map(ExhibitionConverter::toSearchExhibitionResDTO)
                .toList();

        // PageInfo와 MapInfo 생성 - Converter 사용
        return ExhibitionConverter.toSearchExhibitionPageResDTO(
                items, exhibitionPage, SEOUL_CENTER_LAT, SEOUL_CENTER_LNG);
    }

    @Override
    public List<ExhibitionResDTO.HotNowExhibitionResDTO> getHotNowExhibition(String email) {
        LocalDate currentDate = LocalDate.now();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        Pageable topOne = PageRequest.of(0, 4);

        List<Exhibition> exhibitions = exhibitionRepository.findHotNowExhibition(currentDate, topOne, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        return exhibitions.stream()
                .map(exhibition -> {
                    boolean isLiked = exhibitionLikeRepository
                            .existsByUserIdAndExhibitionId(
                                    exhibition.getUser().getId(),
                                    exhibition.getId()
                            );
                    return ExhibitionConverter.toHotNowExhibitionResDTO(exhibition, isLiked);
                })
                .toList();
    }

    @Override
    public ExhibitionResDTO.UpcomingPopularityExhibitionResDTO getUpcomingPopularExhibition() {
        LocalDate currentDate = LocalDate.now();

        Pageable topOne = PageRequest.of(0, 1);
        List<Exhibition> exhibitions = exhibitionRepository.findUpcomingPopularExhibition(currentDate, topOne, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
        Exhibition upcomingEx = exhibitions.get(0);

        List<String> fileKeys = exhibitionImageRepository.findImageUrlsByExhibitionId(upcomingEx.getId());
        return ExhibitionConverter.toUpcomingPopularityExhibitionResDTO(upcomingEx.getId(), upcomingEx.getTitle(), fileKeys);
    }

    @Override
    public ExhibitionResDTO.PopularRegionExhibitionListResDTO getPopularRegionExhibitions() {
        LocalDate currentDate = LocalDate.now();

        Pageable topFour = PageRequest.of(0, 4);
        List<Exhibition> exhibitions = exhibitionRepository.findTopByDistrict(currentDate, topFour, Status.APPROVED);
        if (exhibitions.isEmpty()) {
            throw new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        } else if (exhibitions.size() < 4) {
            log.info("size : {}", exhibitions.size());
            throw new ExhibitionException(ExhibitionErrorCode.DIFFERENT_EXHIBITION_NOT_FOUND);
        }
        List<ExhibitionResDTO.PopularRegionExhibitionResDTO> popularRegionExhibitionResDTOs = exhibitions.stream()
                .map(ExhibitionConverter::toPopularRegionExhibitionResDTO)
                .toList();

        return ExhibitionConverter.toPopularRegionExhibitionListResDTO(popularRegionExhibitionResDTOs);
    }

    @Override
    public List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtiePicks(String email) {
        LocalDate today = LocalDate.now();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        List<Exhibition> candidates = exhibitionRepository.findUnpopularCandidates(today, 20); // 후보 20개

        // 날짜 기반 고정 셔플(하루 동안 결과 고정)
        long seed = today.toEpochDay(); // 필요하면 +고정 salt
        java.util.Random r = new java.util.Random(seed);
        java.util.Collections.shuffle(candidates, r);

        return candidates.stream()
                .limit(4)
                .map(exhibition -> {
                    boolean isLiked = exhibitionLikeRepository.existsByUserIdAndExhibitionId(user.getId(), exhibition.getId());
                    return ExhibitionConverter.toArtieRecommendationResDTO(exhibition, isLiked);
                })
                .toList();
    }

}
