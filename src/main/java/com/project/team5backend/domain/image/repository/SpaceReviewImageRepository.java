package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceReviewImageRepository extends JpaRepository<SpaceReviewImage, Long> {
}