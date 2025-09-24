package com.project.team5backend.domain.image.converter;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.image.entity.ExhibitionImage;
import com.project.team5backend.domain.image.entity.ExhibitionReviewImage;
import com.project.team5backend.domain.image.entity.SpaceImage;
import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.space.entity.Space;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageConverter {
    //전시이미지 생성
    public static ExhibitionImage toExhibitionImage(Exhibition exhibition, String fileKey) {
        return ExhibitionImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .exhibition(exhibition)
                .build();
    }
    //전시리뷰이미지 생성
    public static ExhibitionReviewImage toExhibitionReviewImage(ExhibitionReview review, String fileKey) {
        return ExhibitionReviewImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .exhibitionReview(review)
                .build();
    }
    // Space
    public static SpaceImage toSpaceImage(Space space, String fileKey) {
        return SpaceImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .space(space)
                .build();
    }

    // SpaceReview
    public static SpaceReviewImage toSpaceReviewImage(SpaceReview spaceReview, String fileKey) {
        return SpaceReviewImage.builder()
                .fileKey(fileKey)
                .isDeleted(false)
                .spaceReview(spaceReview)
                .build();
    }
}
