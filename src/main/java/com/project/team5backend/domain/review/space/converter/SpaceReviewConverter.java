package com.project.team5backend.domain.review.space.converter;

import com.project.team5backend.domain.review.space.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpaceReviewConverter {

    public static SpaceReview toSpaceReview(SpaceReviewReqDTO.SpaceReviewCreateReqDTO spaceReviewCreateReqDTO, Space space, User use) {
        return SpaceReview.builder()
                .content(spaceReviewCreateReqDTO.content())
                .rate(spaceReviewCreateReqDTO.rate())
                .isDeleted(false)
                .space(space)
                .user(use)
                .build();
    }

    public static SpaceReviewResDTO.SpaceReviewCreateResDTO toSpaceReviewCreateResDTO(Long spaceReviewId){
        return SpaceReviewResDTO.SpaceReviewCreateResDTO.builder()
                .spaceReviewId(spaceReviewId)
                .message("공간 리뷰가 생성되었습니다.")
                .build();
    }

    public static SpaceReviewResDTO.SpaceReviewDetailResDTO toSpaceReviewDetailResDTO(SpaceReview spaceReview, List<String> imageUrls) {
        return SpaceReviewResDTO.SpaceReviewDetailResDTO.builder()
                .spaceReviewId(spaceReview.getId())
                .rate(spaceReview.getRate())
                .content(spaceReview.getContent())
                .imageUrls(imageUrls)
                .createdAt(spaceReview.getCreatedAt())
                .userName(spaceReview.getUser().getName())
                .build();
    }
}
