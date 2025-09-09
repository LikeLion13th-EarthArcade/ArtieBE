package com.project.team5backend.domain.space.review.service.query;


import com.project.team5backend.domain.space.review.dto.response.SpaceReviewResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceReviewQueryService {
    Page<SpaceReviewResDTO.DetailSpaceReviewResDTO> getSpaceReviewList(long spaceId, Pageable pageable);
    SpaceReviewResDTO.DetailSpaceReviewResDTO getSpaceReviewDetail(long spaceReviewId);
}