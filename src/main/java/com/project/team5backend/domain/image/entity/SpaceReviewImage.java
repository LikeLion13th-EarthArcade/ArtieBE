package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.global.entity.BaseCreateDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpaceReviewImage extends BaseCreateDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_key")
    private String fileKey;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private SpaceReview spaceReview;

    public void deleteImage() {
        isDeleted = true;
        markDeleted(); // 삭제 시간 정보 생성
    }

}

