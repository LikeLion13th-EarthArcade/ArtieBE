package com.project.team5backend.domain.exhibition.entity;

import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionType;
import com.project.team5backend.domain.facility.entity.ExhibitionFacility;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.BaseTimeEntity;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Exhibition extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portal_exhibition_id", unique = true)
    private Long portalExhibitionId; // 문화포털에서 가져온 전시 객체 구분용

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "operating_info", nullable = false)
    private String operatingInfo; // 운영 시간

    @Embedded
    private Address address;

    @Column(name = "exhibition_category", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExhibitionCategory exhibitionCategory;

    @Column(name = "exhibition_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExhibitionType exhibitionType;

    @Column(name = "exhibition_mood", nullable = false)
    @Enumerated(EnumType.STRING)
    private ExhibitionMood exhibitionMood;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "rating_avg", nullable = false)
    private double ratingAvg;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "review_sum", nullable = false)
    private int reviewSum;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExhibitionFacility> exhibitionFacilities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void softDelete() {
        isDeleted = true;
        markDeleted();
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

    public void approveExhibition() {
        this.status = Status.APPROVED;
    }

    public void rejectExhibition() {
        this.status = Status.REJECTED;
    }
}
