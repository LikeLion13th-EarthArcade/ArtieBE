package com.project.team5backend.domain.review.exhibition.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import static com.project.team5backend.global.constant.valid.MessageConstant.BLANK_REVIEW_RATE;


public class ExhibitionReviewReqDTO {
    @Builder
    public record createExReviewReqDTO(
            @Max(value = 5, message = "평점은 최대 5점까지 가능합니다")
            @Min(value = 1, message = "평점은 최소 1점부터 가능합니다")
            @NotNull(message = BLANK_REVIEW_RATE)
            int rate,
            String content
    ) {}
}
