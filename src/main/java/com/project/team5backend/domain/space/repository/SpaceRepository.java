package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>,SpaceRepositoryCustom {
    @Query("""
        select s
        from Space s
        where s.id =:spaceId
          and s.isDeleted = false
          and s.status =:status
        """)
    Optional<Space> findByIdAndIsDeletedFalseAndStatusApproved(@Param("spaceId") long spaceId,@Param("status") Status status);

    @Query("""
        select s
        from Space s
        where s.id =:spaceId
          and s.isDeleted = false
        """)
    Optional<Space> findByIdAndIsDeletedFalse(@Param("spaceId") long spaceId);

    // 리뷰 평균/카운트 갱신
    @Modifying
    @Query("""
        update Space s
        set s.reviewCount = s.reviewCount + 1,
            s.ratingAvg = ((s.ratingAvg * (s.reviewCount - 1)) + :rating) / (s.reviewCount)
        where s.id =:spaceId
        """)
    void applyReviewCreated(@Param("spaceId") Long spaceId, @Param("rating")  double rating);

    @Modifying
    @Query("""
        update Space s
        set s.reviewCount = s.reviewCount - 1,
            s.ratingAvg   = case
                              when s.reviewCount <= 0
                                then 0
                              else ((s.ratingAvg * (s.reviewCount + 1)) - :rating) / s.reviewCount
                             end
        where s.id =:spaceId and s.reviewCount > 0
        """)
    void applySpaceReviewDeleted(@Param("spaceId") Long spaceId, @Param("rating")  double rating);


}