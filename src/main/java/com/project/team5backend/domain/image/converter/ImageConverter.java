package com.project.team5backend.domain.image.converter;

import com.project.team5backend.domain.exhibition.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.review.entity.ExhibitionReview;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.entity.ReviewImage;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.domain.space.space.entity.Space;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageConverter {
    //전시이미지 생성
    public static ExhibitionImage toEntityExhibitionImage(Exhibition exhibition, String imageUrl) {
        return ExhibitionImage.builder()
                .imageUrl(imageUrl)
                .isDeleted(false)
                .exhibition(exhibition)
                .build();
    }
    //전시리뷰이미지 생성
    public static ExhibitionReviewImage toEntityExhibitionReviewImage(ExhibitionReview review, String imageUrl) {
        return ExhibitionReviewImage.builder()
                .imageUrl(imageUrl)
                .isDeleted(false)
                .exhibitionReview(review)
                .build();
    }
    // Space
    public static SpaceImage toEntitySpaceImage(Space space, String imageUrl) {
        return SpaceImage.builder()
                .imageUrl(imageUrl)
                .isDeleted(false)
                .space(space)
                .build();
    }

    // Review
    public static ReviewImage toEntityReviewImage(Review review, String imageUrl) {
        return ReviewImage.builder()
                .imageUrl(imageUrl)
                .isDeleted(false)
                .review(review)
                .build();
    }
}
