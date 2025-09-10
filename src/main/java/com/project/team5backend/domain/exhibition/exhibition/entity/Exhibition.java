package com.project.team5backend.domain.exhibition.exhibition.entity;

import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.BaseTimeEntity;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Exhibition extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_id")
    private Long id;

    private String title;

    private String description;

    private String thumbnail;

    private Integer price;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(nullable = false)
    private LocalTime openTime; // 운영 시작 시간
    @Column(nullable = false)
    private LocalTime closeTime; // 운영 종료 시간

    private String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private ExhibitionCategory exhibitionCategory;

    @Enumerated(EnumType.STRING)
    private ExhibitionType exhibitionType;

    @Enumerated(EnumType.STRING)
    private ExhibitionMood exhibitionMood;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Address address;

    @Column(name = "rating_avg", columnDefinition = "DECIMAL(4,2) NOT NULL DEFAULT 0")
    private BigDecimal ratingAvg;

    @Column(name = "rating_count")
    private Integer reviewCount;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "total_review_score")
    private Integer totalReviewScore;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExhibitionFacility> exhibitionFacilities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void delete() {
        isDeleted = true;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void resetCount() {
        this.likeCount = 0;
        this.reviewCount = 0;
    }

    public void approveStatus() {
        this.status = Status.APPROVED;
    }

    public void rejectStatus() {
        this.status = Status.REJECTED;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void approveExhibition() {
        this.status = Status.APPROVED;
    }

    public void rejectExhibition() {
        this.status = Status.REJECTED;
    }
}
