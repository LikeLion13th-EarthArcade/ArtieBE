package com.project.team5backend.domain.exhibition.exhibition.service.query;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Category;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.Mood;
import com.project.team5backend.global.entity.enums.Sort;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionQueryService {

    ExhibitionResDTO.DetailExhibitionResDTO getDetailExhibition(Long id);

    // 전시 검색
    ExhibitionResDTO.SearchExhibitionPageResDTO searchExhibition(Category category, String district, Mood mood, LocalDate date, Sort sort, int page);

    // 지금뜨는 전시회
    List<ExhibitionResDTO.HotNowExhibitionResDTO> getHotNowExhibition(String email);

    // 다가오는 인기있는 전시회(좋아요 수)
    ExhibitionResDTO.UpcomingPopularityExhibitionResDTO getUpcomingPopularExhibition();

    // 지금 뜨는 지역별 전시회
    ExhibitionResDTO.PopularRegionExhibitionListResDTO getPopularRegionExhibitions();

    // artie 픽
    List<ExhibitionResDTO.ArtieRecommendationResDTO> getTodayArtiePicks(String email);
}
