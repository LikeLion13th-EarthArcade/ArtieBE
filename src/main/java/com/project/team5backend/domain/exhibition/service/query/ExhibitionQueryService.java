package com.project.team5backend.domain.exhibition.service.query;

import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.global.entity.enums.Sort;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionQueryService {

    ExhibitionResDTO.ExhibitionDetailResDTO findExhibitionDetail(Long id);

    // 전시 검색
    ExhibitionResDTO.SearchExhibitionPageResDTO searchExhibition(ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate date, Sort sort, int page);

    // 지금뜨는 전시회
    List<ExhibitionResDTO.HotNowExhibitionResDTO> getHotNowExhibition(String email);

    // 다가오는 인기있는 전시회(좋아요 수)
    ExhibitionResDTO.UpcomingPopularityExhibitionResDTO getUpcomingPopularExhibition();

    // 지금 뜨는 지역별 전시회
    ExhibitionResDTO.PopularRegionExhibitionListResDTO getPopularRegionExhibitions();

    // artie 픽
    List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtiePicks(String email);
}
