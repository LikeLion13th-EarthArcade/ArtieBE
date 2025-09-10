package com.project.team5backend.domain.space.review.converter;

import com.project.team5backend.domain.space.review.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.space.review.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.space.review.entity.SpaceReview;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpaceReviewConverter {

    public static SpaceReview toSpaceReview(SpaceReviewReqDTO.CreateSpaceReviewReqDTO request, Space space, User use) {
        return SpaceReview.builder()
                .content(request.content())
                .rating(request.rating())
                .isDeleted(false)
                .space(space)
                .user(use)
                .build();
    }

    public static SpaceReviewResDTO.CreateSpaceReviewResDTO toCreateSpaceReviewResDTO(long spaceReviewId){
        return SpaceReviewResDTO.CreateSpaceReviewResDTO.builder()
                .spaceReviewId(spaceReviewId)
                .message("공간 리뷰가 생성되었습니다.")
                .build();
    }

    public static SpaceReviewResDTO.DetailSpaceReviewResDTO toDetailSpaceReviewResDTO(SpaceReview spaceReview, List<String> imageUrls) {
        return SpaceReviewResDTO.DetailSpaceReviewResDTO.builder()
                .spaceReviewId(spaceReview.getId())
                .rating(spaceReview.getRating())
                .content(spaceReview.getContent())
                .imageUrls(imageUrls)
                .createdAt(spaceReview.getCreatedAt())
                .userName(spaceReview.getUser().getName())
                .build();
    }

//    public SpaceReviewResDTO.ReviewListResponse toReviewListResponse(SpaceReview spaceReview) {
//        String thumbnailUrl = spaceReview.getMainImageKey();  // S3 URL 그대로 사용
//
//        return new SpaceReviewResDTO.ReviewListResponse(
//                spaceReview.getId(),
//                spaceReview.getUser().getName(),
//                spaceReview.getRating(),
//                spaceReview.getContent(),
//                thumbnailUrl,
//                spaceReview.getImageUrls().size(),
//                spaceReview.getCreatedAt()
//        );
//    }
//
//    public List<SpaceReviewResDTO.ReviewListResponse> toReviewListResponseList(List<SpaceReview> spaceReviews) {
//        return spaceReviews.stream()
//                .map(this::toReviewListResponse)
//                .collect(Collectors.toList());
//    }
//
//    public SpaceReviewResDTO.ReviewDetailResponse toReviewDetailResponse(SpaceReview spaceReview) {
//        List<String> imageUrls = spaceReview.getImageUrls(); // 이미 S3 URL 저장됨
//
//        return new SpaceReviewResDTO.ReviewDetailResponse(
//                spaceReview.getId(),
//                spaceReview.getUser().getName(),
//                spaceReview.getRating(),
//                spaceReview.getContent(),
//                imageUrls,
//                spaceReview.getCreatedAt()
//        );
//    }
}
