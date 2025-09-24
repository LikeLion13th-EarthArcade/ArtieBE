package com.project.team5backend.domain.review.space.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static com.project.team5backend.global.constant.valid.MessageConstant.BLANK_REVIEW_RATE;

public class SpaceReviewReqDTO {

    public record SpaceReviewCreateReqDTO(
            @Max(value = 5, message = "평점은 최대 5점까지 가능합니다")
            @Min(value = 1, message = "평점은 최소 1점부터 가능합니다")
            @NotNull(message = BLANK_REVIEW_RATE)
            int rate,
            String content
    ){}
}