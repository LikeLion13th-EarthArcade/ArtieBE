package com.project.team5backend.domain.review.exhibition.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;


public class ExhibitionReviewReqDTO {
    @Builder
    public record createExReviewReqDTO(
            @NotNull
            @DecimalMax(value = "5.0", message = "평점은 최대 5.0점까지 가능합니다")
            @DecimalMin(value = "0.0", message = "평점은 최소 0.0점부터 가능합니다")
            Double rating,
            String content
    ) {}
}
