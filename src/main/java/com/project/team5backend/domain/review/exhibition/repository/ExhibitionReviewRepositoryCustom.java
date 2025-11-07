package com.project.team5backend.domain.review.exhibition.repository;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionReviewRepositoryCustom {
    Page<ExhibitionReview> findReviewsByTargetId(Long exhibitionId, ReviewSearchType type, Sort sort, Pageable pageable);
}
