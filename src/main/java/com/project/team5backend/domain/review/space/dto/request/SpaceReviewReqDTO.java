package com.project.team5backend.domain.review.space.dto.request;

public class SpaceReviewReqDTO {

    public record CreateSpaceReviewReqDTO(
            Double rating,
            String content
    ){}
}