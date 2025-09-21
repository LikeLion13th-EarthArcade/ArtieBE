package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.global.entity.BaseCreateDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExhibitionReviewImage extends BaseCreateDeleteEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_review_image_id")
    private Long id;

    private String fileKey;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_review_id")
    private ExhibitionReview exhibitionReview;

    public void deleteImage() {
        isDeleted = true;
        markDeleted();
    }
}