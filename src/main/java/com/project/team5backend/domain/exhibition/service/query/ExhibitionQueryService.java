package com.project.team5backend.domain.exhibition.service.query;

import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.team5backend.domain.common.enums.StatusGroup;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionQueryService {

    ExhibitionResDTO.ExhibitionDetailResDTO findExhibitionDetail(Long userId, Long exhibitionId);

    // 전시 검색
    ExhibitionResDTO.ExhibitionSearchPageResDTO searchExhibitions(ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate date, Sort sort, int page, int size);

    // 지금뜨는 전시회
    List<ExhibitionResDTO.ExhibitionHotNowResDTO> getHotNowExhibitions(Long userId);

    // 다가오는 인기있는 전시회(좋아요 수)
    ExhibitionResDTO.UpcomingPopularExhibitionResDTO getUpcomingPopularExhibition();

    // 지금 뜨는 지역별 전시회
    ExhibitionResDTO.RegionalPopularExhibitionListResDTO getRegionalPopularExhibitions();

    // artie 픽
    List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtieRecommendations(Long userId);

    //내가 등록한 전시
    Page<ExhibitionResDTO.ExhibitionSummaryResDTO> getSummaryExhibitionList(Long userId, StatusGroup status, Sort sort, int page, int size);

    //내가 등록한 전시 조회
    ExhibitionResDTO.MyExhibitionDetailResDTO getMyDetailExhibition(Long userId, Long exhibitionId);


    Page<ExhibitionResDTO.ExhibitionLikeSummaryResDTO> getInterestedExhibitions(Long userId, Pageable pageable);
}
