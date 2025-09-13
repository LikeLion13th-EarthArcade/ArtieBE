package com.project.team5backend.domain.review.exhibition.repository;

import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExhibitionReviewRepository extends JpaRepository<ExhibitionReview, Long> {
    @Modifying
    @Query("update ExhibitionReview er set er.isDeleted = true where er.exhibition.id =:exhibitionId")
    void softDeleteByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    @Query("""
        select sr from SpaceReview sr
        join fetch sr.user u
        left join sr.spaceReviewImages eri
        where sr.id =:spaceReviewId and sr.isDeleted is false
    """)
    Optional<ExhibitionReview> findByIdAndIsDeletedFalse(@Param("spaceReviewId") Long spaceReviewId);

    @Query("""
        SELECT DISTINCT sr FROM SpaceReview sr
        JOIN FETCH sr.user u
        LEFT JOIN sr.spaceReviewImages sri
        WHERE sr.space.id =:spaceId AND sr.isDeleted = false
        ORDER BY sr.createdAt DESC
        """)
    Page<ExhibitionReview> findByExhibitionIdAndIsDeletedFalse(@Param("exhibitionId") Long exhibitionId, Pageable pageable);
}
