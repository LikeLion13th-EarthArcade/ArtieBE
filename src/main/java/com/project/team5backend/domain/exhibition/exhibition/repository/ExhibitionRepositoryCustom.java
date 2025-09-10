package com.project.team5backend.domain.exhibition.exhibition.repository;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.entity.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExhibitionRepositoryCustom {
    Page<Exhibition> findExhibitionsWithFilters(ExhibitionCategory exhibitionCategory, String district, ExhibitionMood exhibitionMood, LocalDate localDate, Sort sort, Pageable pageable);

    List<Exhibition> findUnpopularCandidates(LocalDate today, int limit);

    Page<Exhibition> findAdminExhibitionsByStatus(StatusGroup status, Pageable pageable);
}
