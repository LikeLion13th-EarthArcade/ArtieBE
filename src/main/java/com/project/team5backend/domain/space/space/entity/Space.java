package com.project.team5backend.domain.space.space.entity;

import com.project.team5backend.domain.space.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.space.entity.enums.SpacePurpose;
import com.project.team5backend.domain.space.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.space.entity.enums.SpaceType;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.BaseTimeEntity;
import com.project.team5backend.global.entity.embedded.Address;
import com.project.team5backend.global.entity.enums.Status;
import com.project.team5backend.domain.facility.entity.SpaceFacility;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
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

    @Column(nullable = false)
    private String name; // 공간 이름

    @Column(nullable = false, length = 500)
    private String description; // 공간 설명

    @Column(nullable = false)
    private String thumbnail; // 썸네일

    @Column(nullable = false)
    private LocalTime openTime; // 운영 시작 시간
    @Column(nullable = false)
    private LocalTime closeTime; // 운영 종료 시간

    private String websiteUrl; // 웹사이트 url

    @Column(length = 20)
    private String phoneNumber; // 전화번호

    @Column(length = 100)
    private String email; // 이메일

    private String snsUrl; // SNS 주소

    @Column(columnDefinition = "DECIMAL(4,2) NOT NULL DEFAULT 0")
    private BigDecimal ratingAvg;

    @Column(nullable = false)
    private Integer reviewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(nullable = false)
    private Integer totalReviewScore = 0;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Embedded
    private Address address; // 공간 주소 (위도, 경도 포함)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceType type;     // 공간 유형

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceSize size;     // 공간 크기

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpacePurpose purpose; // 공간 목적

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpaceMood mood;     // 공간 분위기

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 승인 상태

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpaceFacility> spaceFacilities = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

}
