package com.project.team5backend.domain.review.space.repository;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceReviewRepositoryCustom {
    Page<SpaceReview> findReviewsByTargetId(Long targetId, ReviewSearchType type, Sort sort, Pageable pageable);
}
