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
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.entity.enums.Sort;
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
public class ExhibitionQueryServiceImpl implements ExhibitionQueryService {

    private static final int PAGE_SIZE = 4;
    private static final double SEOUL_CITY_HALL_LAT = 37.5665;
    private static final double SEOUL_CITY_HALL_LNG = 126.9780;

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionImageRepository exhibitionImageRepository;
    private final InteractLogService interactLogService;
    private final ExhibitionLikeRepository exhibitionLikeRepository;
    private final UserRepository userRepository;
    @Override
    public ExhibitionResDTO.ExhibitionDetailResDTO findExhibitionDetail(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findByIdAndIsDeletedFalseAndStatusApprove(exhibitionId, Status.APPROVED)
                .orElseThrow(() -> new ExhibitionException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        // ai 분석을 위한 로그 생성
        interactLogService.logClick(1L, exhibitionId);
        // 전시 이미지들의 fileKey만 조회
        List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(exhibitionId);

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
                .map(ExhibitionConverter::toExhibitionSearchResDTO);

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
                .map(exhibition -> ExhibitionConverter.toHotNowExhibitionResDTO(exhibition, isExhibitionLiked(userId, exhibition.getId())))
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
        List<String> imageUrls = exhibitionImageRepository.findImageUrlsByExhibitionId(upcomingEx.getId());
        return ExhibitionConverter.toUpcomingPopularityExhibitionResDTO(upcomingEx.getId(), upcomingEx.getTitle(), imageUrls);
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

    private boolean isExhibitionLiked(Long userId, Long exhibitionId) {
        return exhibitionLikeRepository.existsByUserIdAndExhibitionId(userId, exhibitionId);
    }
}
