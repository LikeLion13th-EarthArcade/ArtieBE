package com.project.team5backend.domain.exhibition.repository;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionRepositoryCustom {
    Page<Exhibition> findExhibitionsWithFilters(ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate, Sort sort, Pageable pageable);

    List<Exhibition> findUnpopularCandidates(LocalDate today, int limit);

    Page<Exhibition> findAdminExhibitionsByStatus(StatusGroup status, Pageable pageable);

    Page<Exhibition> findMyExhibitionsByStatus(Long userId, StatusGroup status, Pageable pageable);
}
