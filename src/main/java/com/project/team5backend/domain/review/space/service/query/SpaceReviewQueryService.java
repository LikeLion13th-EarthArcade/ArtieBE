package com.project.team5backend.domain.review.space.service.query;


import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.common.enums.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceReviewQueryService {
    Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getSpaceReviews(Long spaceId, Sort sort, Pageable pageable);

    SpaceReviewResDTO.SpaceReviewDetailResDTO getSpaceReviewDetail(Long spaceReviewId);

    Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getMySpaceReviews(Long userId, Sort sort, Pageable pageable);
}