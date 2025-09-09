package com.project.team5backend.domain.space.review.service.query;


import com.project.team5backend.domain.space.review.converter.ReviewConverter;
import com.project.team5backend.domain.space.review.dto.response.ReviewResponse;
import com.project.team5backend.domain.space.review.entity.SpaceReview;
import com.project.team5backend.domain.space.review.repository.SpaceReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceImpl implements ReviewQueryService {
    private final SpaceReviewRepository spaceReviewRepository;
    private final ReviewConverter reviewConverter;

//    @Override
//    public List<ReviewResponse.ReviewListResponse> getReviewsBySpaceId(Long spaceId) {
//        List<SpaceReview> spaceReviews = spaceReviewRepository.findBySpaceId(spaceId);
//        return reviewConverter.toReviewListResponseList(spaceReviews);
//    }
//
//    @Override
//    public ReviewResponse.ReviewDetailResponse getReviewById(Long reviewId) {
//        SpaceReview spaceReview = spaceReviewRepository.findById(reviewId)
//                .orElseThrow(() -> new IllegalArgumentException("SpaceReview not found"));
//        return reviewConverter.toReviewDetailResponse(spaceReview);
//    }
}