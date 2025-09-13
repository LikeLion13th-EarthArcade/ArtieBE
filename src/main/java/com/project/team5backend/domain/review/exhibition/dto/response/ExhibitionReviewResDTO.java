package com.project.team5backend.domain.review.exhibition.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class ExhibitionReviewResDTO {
    @Builder
    public record ExReviewDetailResDTO(
            Long reviewId,
            int rate,
            String content,
            List<String> imageUrls,
            LocalDateTime createdAt,
            String userName
    ) {}

    @Builder
    public record ExReviewCreateResDTO(
            Long reviewId,
            String message
    ){}

}
