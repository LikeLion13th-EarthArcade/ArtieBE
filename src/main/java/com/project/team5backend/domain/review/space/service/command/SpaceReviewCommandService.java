package com.project.team5backend.domain.review.space.service.command;


import com.project.team5backend.domain.review.space.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SpaceReviewCommandService {
    SpaceReviewResDTO.SpaceReviewCreateResDTO createSpaceReview(Long spaceId, Long userId, SpaceReviewReqDTO.SpaceReviewCreateReqDTO request, List<MultipartFile> images);
    void deleteSpaceReview(Long spaceReviewId, Long userId);
}