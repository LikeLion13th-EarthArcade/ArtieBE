package com.project.team5backend.domain.review.exhibition.repository;

import com.project.team5backend.domain.review.exhibition.entity.ExhibitionReview;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExhibitionReviewRepository extends JpaRepository<ExhibitionReview, Long>, ExhibitionReviewRepositoryCustom{
    @Modifying
    @Query("update ExhibitionReview er set er.isDeleted = true where er.exhibition.id =:exhibitionId")
    void softDeleteByExhibitionId(@Param("exhibitionId") Long exhibitionId);

    @Query("""
        select er from ExhibitionReview er
        join er.user u
        left join fetch er.exhibitionReviewImages eri
        where er.id = :exhibitionReviewId
        and er.isDeleted == false
        and u.id = :userId
    """)
    Optional<ExhibitionReview> findByIdAndIsDeletedFalseWithUser(@Param("exhibitionReviewId") Long exhibitionReviewId, @Param("userId") Long userId);

    @Query("""
        select er from ExhibitionReview er
        left join fetch er.exhibitionReviewImages eri
        where er.id =:exhibitionReviewId
        and er.isDeleted == false
    """)
    Optional<ExhibitionReview> findByIdAndIsDeletedFalse(@Param("exhibitionReviewId") Long exhibitionReviewId);

    @Query("""
        SELECT DISTINCT er FROM ExhibitionReview er
        JOIN FETCH er.user u
        LEFT JOIN er.exhibitionReviewImages sri
        WHERE er.exhibition.id =:exhibitionId AND er.isDeleted = false
        ORDER BY er.createdAt DESC
        """)
    Page<ExhibitionReview> findByExhibitionIdAndIsDeletedFalse(@Param("exhibitionId") Long exhibitionId, Pageable pageable);

    @Query("""
        select er
        from ExhibitionReview er
        join fetch er.user u
        left join er.exhibitionReviewImages eri
        where er.isDeleted is false and er.user = :user
    """)
    Page<ExhibitionReview> findMyExReviewsByUserIdAndIsDeletedFalse(@Param("user") User user, Pageable pageable);
}
