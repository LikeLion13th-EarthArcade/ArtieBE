package com.project.team5backend.domain.review.exhibition.service.query;


import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionReviewQueryService {
    ExhibitionReviewResDTO.ExReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId);

    Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviews(Long exhibitionId, Sort sort, Pageable pageable);

    Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getMyExhibitionReviews(Long userId, Sort sort, Pageable pageable);
}
