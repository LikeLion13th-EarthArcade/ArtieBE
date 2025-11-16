package com.project.team5backend.domain.review.exhibition;

import com.project.team5backend.domain.common.enums.ReviewSearchType;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.exhibition.repository.ExhibitionReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExhibitionReviewReader {

    private final ExhibitionReviewRepository exhibitionReviewRepository;

    public Page<ExhibitionReview> readByExhibition(Long exhibitionId, Sort sort, Pageable pageable) {
        return exhibitionReviewRepository.findReviewsByTargetId(
                exhibitionId, ReviewSearchType.EXHIBITION, sort, pageable
        );
    }

    public Page<ExhibitionReview> readByUser(Long userId, Sort sort, Pageable pageable) {
        return exhibitionReviewRepository.findReviewsByTargetId(
                userId, ReviewSearchType.USER, sort, pageable
        );
    }
}
