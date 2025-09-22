package com.project.team5backend.domain.space.entity;

import jakarta.persistence.*;

@Entity
public class SpaceVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_verification_id")
    private Long id;

    private String businessNumber;

    private String businessLicenseKey;

    private String buildingRegisterKey;
}
