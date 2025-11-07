package com.project.team5backend.domain.review.space.repository;


import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.review.space.entity.SpaceReview;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceReviewRepository extends JpaRepository<SpaceReview, Long>, SpaceReviewRepositoryCustom {
    @Modifying
    @Query("update SpaceReview sr set sr.isDeleted = true where sr.space.id =:spaceId")
    void softDeleteBySpaceId(@Param("spaceId") Long spaceId);

    @Query("""
        select sr from SpaceReview sr
        join sr.user u
        left join fetch sr.spaceReviewImages sri
        where sr.id =:spaceReviewId
        and sr.isDeleted = false
        and u.id = :userId
    """)
    Optional<SpaceReview> findByIdAndUserIdAndIsDeletedFalse(@Param("spaceReviewId") Long spaceReviewId, @Param("userId") Long userId);

    @Query("""
        select sr from SpaceReview sr
        join fetch sr.user u
        where sr.id =:spaceReviewId
        and sr.isDeleted = false
    """)
    Optional<SpaceReview> findByIdAndIsDeletedFalse(@Param("spaceReviewId") Long spaceReviewId);

    @Query("""
        SELECT DISTINCT sr FROM SpaceReview sr
        JOIN FETCH sr.user u
        LEFT JOIN sr.spaceReviewImages sri
        WHERE sr.space.id =:spaceId AND sr.isDeleted = false
        ORDER BY sr.createdAt DESC
    """)
    Page<SpaceReview> findBySpaceIdAndIsDeletedFalse(@Param("spaceId") Long spaceId, Pageable pageable);

    @Query("""
        select sr
        from SpaceReview sr
        join fetch sr.user u
        left join sr.spaceReviewImages sri
        where sr.isDeleted = false
        and sr.user = :user
    """)
    Page<SpaceReview> findMySpaceReviewsByIdAndIsDeletedFalse(@Param("user") User user, Pageable pageable);
}