package com.project.team5backend.domain.review.space.service.query;


import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpaceReviewQueryService {
    Page<SpaceReviewResDTO.SpaceReviewDetailResDTO> getSpaceReviewList(Long spaceId, int page);
    SpaceReviewResDTO.SpaceReviewDetailResDTO getSpaceReviewDetail(Long spaceReviewId);
}