package com.project.team5backend.domain.space.review.converter;

import com.project.team5backend.domain.space.review.dto.request.ReviewRequest;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.entity.SpaceReview;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewConverter {

//    public SpaceReview toReview(ReviewRequest.CreateRe request, Space space, User user, String mainImageUrl) {
//        SpaceReview spaceReview = new SpaceReview();
//        spaceReview.setRating(request.rating());
//        spaceReview.setContent(request.content());
//        spaceReview.setMainImageKey(mainImageUrl);  // upload()에서 반환된 URL
//        spaceReview.setSpace(space);
//        spaceReview.setUser(user);
//        return spaceReview;
//    }
//
//    public ReviewResponse.ReviewListResponse toReviewListResponse(SpaceReview spaceReview) {
//        String thumbnailUrl = spaceReview.getMainImageKey();  // S3 URL 그대로 사용
//
//        return new ReviewResponse.ReviewListResponse(
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
//    public List<ReviewResponse.ReviewListResponse> toReviewListResponseList(List<SpaceReview> spaceReviews) {
//        return spaceReviews.stream()
//                .map(this::toReviewListResponse)
//                .collect(Collectors.toList());
//    }
//
//    public ReviewResponse.ReviewDetailResponse toReviewDetailResponse(SpaceReview spaceReview) {
//        List<String> imageUrls = spaceReview.getImageUrls(); // 이미 S3 URL 저장됨
//
//        return new ReviewResponse.ReviewDetailResponse(
//                spaceReview.getId(),
//                spaceReview.getUser().getName(),
//                spaceReview.getRating(),
//                spaceReview.getContent(),
//                imageUrls,
//                spaceReview.getCreatedAt()
//        );
//    }
}
