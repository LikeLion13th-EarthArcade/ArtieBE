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
    private Long id;

    @Column(name = "biz_number", nullable = false)
    private String bizNumber;

    @Column(name = "business_license_key", nullable = false)
    private String businessLicenseKey;

    @Column(name = "building_register_key", nullable = false)
    private String buildingRegisterKey;
}
