package com.project.team5backend.domain.space.review.entity;

import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SpaceReview extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_review_id")
    private Long id;

    private String content;

    private Double rating;

    private boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "spaceReview", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<SpaceReviewImage> spaceReviewImages;

    public void softDelete() {
        this.isDeleted = true;
        markDeleted();
    }
}
