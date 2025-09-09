package com.project.team5backend.domain.space.review.dto.request;

public class SpaceReviewReqDTO {

    public record CreateSpaceReviewReqDTO(
            Double rating,
            String content
    ){}
}