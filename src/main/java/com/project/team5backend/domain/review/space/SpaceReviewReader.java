package com.project.team5backend.domain.review.space;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.review.space.repository.SpaceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpaceReviewReader {

    private final SpaceReviewRepository spaceReviewRepository;

    public Page<SpaceReview> readBySpace(Long spaceId, Sort sort, Pageable pageable) {
        return spaceReviewRepository.findReviewsByTargetId(
                spaceId, ReviewSearchType.EXHIBITION, sort, pageable
        );
    }

    public Page<SpaceReview> readByUser(Long userId, Sort sort, Pageable pageable) {
        return spaceReviewRepository.findReviewsByTargetId(
                userId, ReviewSearchType.USER, sort, pageable
        );
    }
}
