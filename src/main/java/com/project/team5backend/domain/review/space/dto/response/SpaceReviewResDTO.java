package com.project.team5backend.domain.review.space.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class SpaceReviewResDTO {

    @Builder
    public record SpaceReviewCreateResDTO(
            Long spaceReviewId,
            String message
    ){}

    @Builder
    public record SpaceReviewDetailResDTO(
            Long spaceReviewId,
            int rate,
            String content,
            List<String> imageUrls,
            LocalDateTime createdAt,
            String userName
    ) {}
}