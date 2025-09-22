package com.project.team5backend.domain.space.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SpaceVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_verification_id")
    private Long id;

    @Column(nullable = false)
    private String businessNumber;

    @Column(nullable = false)
    private String businessLicenseKey;

    @Column(nullable = false)
    private String buildingRegisterKey;
}
