package com.project.team5backend.domain.image.repository;

import com.project.team5backend.domain.image.entity.SpaceReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SpaceReviewImageRepository extends JpaRepository<SpaceReviewImage, Long> {

    @Query("select ri.imageUrl from SpaceReviewImage ri where ri.spaceReview.id = :reviewId")
    List<String> findFileKeysByReviewId(@Param("reviewId") Long reviewId);
}