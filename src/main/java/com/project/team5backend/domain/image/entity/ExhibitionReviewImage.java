package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.common.BaseCreateDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ExhibitionReviewImage extends BaseCreateDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_review_id", nullable = false)
    private ExhibitionReview exhibitionReview;

    public void deleteImage() {
        isDeleted = true;
        markDeleted();
    }
}