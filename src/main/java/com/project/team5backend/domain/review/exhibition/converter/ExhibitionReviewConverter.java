package com.project.team5backend.domain.review.exhibition.converter;

import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.review.exhibition.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExhibitionReviewConverter {

    public static ExhibitionReview toEntity(ExhibitionReviewReqDTO.createExReviewReqDTO createExReviewReqDTO, Exhibition exhibition, User user) {
        return ExhibitionReview.builder()
                .rate(createExReviewReqDTO.rate())
                .content(createExReviewReqDTO.content())
                .exhibition(exhibition)
                .user(user)
                .isDeleted(false)
                .build();
    }

    public static ExhibitionReviewResDTO.ExReviewCreateResDTO toExReviewCreateResDTO(Long exReviewId){
        return ExhibitionReviewResDTO.ExReviewCreateResDTO.builder()
                .reviewId(exReviewId)
                .message("리뷰가 생성되었습니다.")
                .build();
    }

    public static ExhibitionReviewResDTO.ExReviewDetailResDTO toExReviewDetailResDTO(ExhibitionReview review, List<String> imageUrls) {
        return ExhibitionReviewResDTO.ExReviewDetailResDTO.builder()
                .reviewId(review.getId())
                .rate(review.getRate())
                .content(review.getContent())
                .imageUrls(imageUrls)
                .createdAt(review.getCreatedAt())
                .userName(review.getUser().getName())
                .build();
    }
}
