package com.project.team5backend.domain.space.review.repository;


import com.project.team5backend.domain.space.review.entity.SpaceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceReviewRepository extends JpaRepository<SpaceReview, Long> {
    List<SpaceReview> findBySpaceId(Long spaceId);

    @Modifying
    @Query("update SpaceReview sr set sr.isDeleted = true where sr.space.id =:spaceId")
    void softDeleteBySpaceId(@Param("spaceId") Long spaceId);

}