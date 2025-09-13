package com.project.team5backend.domain.review.space.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class SpaceReviewReqDTO {

    public record SpaceReviewCreateReqDTO(
            @Max(value = 5, message = "평점은 최대 5점까지 가능합니다")
            @Min(value = 1, message = "평점은 최소 1점부터 가능합니다")
            int rate,
            String content
    ){}
}