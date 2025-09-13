package com.project.team5backend.domain.review.space.repository;


import com.project.team5backend.domain.review.space.entity.SpaceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceReviewRepository extends JpaRepository<SpaceReview, Long> {
    @Modifying
    @Query("update SpaceReview sr set sr.isDeleted = true where sr.space.id =:spaceId")
    void softDeleteBySpaceId(@Param("spaceId") Long spaceId);

    @Query("SELECT DISTINCT sr FROM SpaceReview sr " +
            "WHERE sr.space.id =:spaceId AND sr.isDeleted = false " +
            "ORDER BY sr.createdAt DESC")
    Page<SpaceReview> findBySpaceIdAndIsDeletedFalse(@Param("spaceId") long spaceId, Pageable pageable);

    @Query("select sr from SpaceReview sr where sr.id=:spaceReviewId and sr.isDeleted is false")
    Optional<SpaceReview> findByIdAndIsDeletedFalse(@Param("spaceReviewId") long spaceReviewId);
}