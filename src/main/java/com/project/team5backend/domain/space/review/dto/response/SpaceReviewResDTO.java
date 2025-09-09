package com.project.team5backend.domain.space.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class SpaceReviewResDTO {

    @Builder
    public record CreateSpaceReviewResDTO (
            Long spaceReviewId,
            String message
    ){}

    @Builder
    public record DetailSpaceReviewResDTO(
            Long spaceReviewId,
            Double rating,
            String content,
            List<String> imageUrls,
            LocalDateTime createdAt,
            String userName
    ) {}
    public record ReviewDetailResponse(
            Long reviewId,
            String name,
            Double rating,
            String content,
            List<String> images,
            LocalDateTime createdAt
    ){}
}