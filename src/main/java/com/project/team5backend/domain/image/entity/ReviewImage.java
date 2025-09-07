package com.project.team5backend.domain.image.entity;

import com.project.team5backend.domain.space.review.entity.Review;
import com.project.team5backend.global.entity.BaseCreateDeleteEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewImage extends BaseCreateDeleteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public void deleteImage() {
        isDeleted = true;
        markDeleted(); // 삭제 시간 정보 생성
    }

}

