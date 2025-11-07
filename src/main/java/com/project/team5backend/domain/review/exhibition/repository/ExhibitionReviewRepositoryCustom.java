package com.project.team5backend.domain.review.exhibition.repository;

import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface ExhibitionReviewRepositoryCustom {
    Page<ExhibitionReview> findByExhibitionIdAndIsDeletedFalse(@Param("exhibitionId") Long exhibitionId, Sort sort, Pageable pageable);
}
