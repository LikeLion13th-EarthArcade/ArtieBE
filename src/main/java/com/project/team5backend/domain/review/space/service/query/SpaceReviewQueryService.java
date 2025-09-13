package com.project.team5backend.domain.review.space.service.query;


import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceReviewQueryService {
    Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getSpaceReviewList(long spaceId, Pageable pageable);
    SpaceReviewResDTO.SpaceReviewDetailResDTO getSpaceReviewDetail(long spaceReviewId);
}