package com.project.team5backend.domain.space.entity;

import com.project.team5backend.domain.common.BaseTimeEntity;
import com.project.team5backend.domain.common.embedded.Address;
import com.project.team5backend.domain.common.enums.Status;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Space extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 공간 이름

    @Column(name = "description", nullable = false)
    private String description; // 공간 설명

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail; // 썸네일

    @Column(name = "operating_info", nullable = false)
    private String operatingInfo; // 운영 시간

    @Column(name = "website_url")
    private String websiteUrl; // 웹사이트 url

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // 전화번호

    @Column(name = "email", nullable = false)
    private String email; // 이메일

    @Column(name = "sns_url")
    private String snsUrl; // SNS 주소

    @Column(name = "rating_avg", nullable = false)
    private double ratingAvg;

    @Column(name = "reveiw_count", nullable = false)
    private int reviewCount;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Column(name = "review_sum", nullable = false)
    private int reviewSum;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "application_method", nullable = false)
    private String applicationMethod;

    @Embedded
    private Address address; // 공간 주소 (위도, 경도 포함)

    @Column(name = "space_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceType spaceType;     // 공간 유형

    @Column(name = "space_size", nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceSize spaceSize;     // 공간 크기

    @Column(name = "space_mood", nullable = false)
    @Enumerated(EnumType.STRING)
    private SpaceMood spaceMood;     // 공간 분위기

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status; // 승인 상태

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SpaceFacility> spaceFacilities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private SpaceVerification spaceVerification;

    public void softDelete() {
        this.isDeleted = true;
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

    public void approveSpace() {
        this.status = Status.APPROVED;
    }

    public void rejectSpace() {
        this.status = Status.REJECTED;
    }

}
