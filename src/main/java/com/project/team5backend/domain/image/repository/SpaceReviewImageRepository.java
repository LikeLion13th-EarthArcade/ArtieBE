package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceReviewImageRepository extends JpaRepository<SpaceReviewImage, Long> {
    List<SpaceReviewImage> findBySpaceReviewId(long spaceReviewId);
}