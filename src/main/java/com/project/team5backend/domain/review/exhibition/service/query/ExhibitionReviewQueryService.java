package com.project.team5backend.domain.review.exhibition.service.query;

import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionReviewQueryService {
    ExhibitionReviewResDTO.ExReviewDetailResDTO getExhibitionReviewDetail(Long exhibitionReviewId);

    Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviewList(Long exhibitionId, Pageable pageable);
}
