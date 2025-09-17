package com.project.team5backend.domain.exhibition.repository;

import com.project.team5backend.domain.admin.dashboard.dto.response.ExhibitionSummaryResDTO;
import com.project.team5backend.domain.exhibition.entity.Exhibition;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, ExhibitionRepositoryCustom {
    //삭제되지 않았고, 승인된 전시
    @Query("""
        select e from Exhibition e
        join fetch e.user
        where e.id = :exhibitionId
        and e.isDeleted = false
        and e.status =:status
    """)
    Optional<Exhibition> findByIdAndIsDeletedFalseAndStatusApprove(@Param("exhibitionId") Long exhibitionId, @Param("status") Status status);

    @Query("""
        select e from Exhibition e
        where e.id = :exhibitionId
        and e.isDeleted = false
    """)
    Optional<Exhibition> findByIdAndIsDeletedFalse(@Param("exhibitionId") Long exhibitionId);

    //삭제되지않았고, 승인되고, 진행중인 전시
    @Query("""
        select e from Exhibition e
        where e.id =:exhibitionId
        and e.isDeleted = false
        and e.status =:status
        and e.startDate <=:current
        and e.endDate >=:current
    """)
    Optional<Exhibition> findByIdAndIsDeletedFalseAndStatusApproveAndOpening(Long exhibitionId, LocalDate current, @Param("status") Status status);
    // 지금 뜨는 전시회
    @Query("""
        select e from Exhibition e
        where e.isDeleted = false
        and e.status =:status
        and e.startDate <=:current
        and e.endDate >=:current
        order by e.reviewCount desc , e.createdAt desc
   """)
    List<Exhibition> findExhibitionHotNow(@Param("current") LocalDate current, Pageable pageable, @Param("status") Status status);

    // 다가오는, 지금뜨는 전시회
    @Query("""
        select e from Exhibition e
        where e.isDeleted = false
        and e.startDate > :current
        and e.status =:status
        order by e.likeCount desc, e.createdAt desc
        """)
    List<Exhibition> findUpcomingPopularExhibition(@Param("current") LocalDate current, Pageable pageable, @Param("status") Status status);

    // 지금 뜨는 지역구 전시회
    @Query(value = """
    WITH ranked AS (
      SELECT e.*,
            ROW_NUMBER() OVER (
             PARTITION BY e.district
             ORDER BY e.review_count DESC, e.updated_at DESC, e.exhibition_id DESC
           ) rn
      FROM exhibition e
     WHERE e.is_deleted = false
       AND e.start_date <= :current
       AND e.end_date   >= :current
       AND e.status =:status
    )
    SELECT * FROM ranked
    WHERE rn = 1
    ORDER BY review_count DESC, updated_at DESC, exhibition_id DESC
    """, nativeQuery = true)
    List<Exhibition> findTopByDistrict(@Param("current") LocalDate current, Pageable pageable, @Param("status") Status status);

    // 리뷰 평균/카운트 갱신
    @Modifying
    @Query("""
        update Exhibition e
        set e.reviewCount = e.reviewCount + 1,
            e.reviewSum   = e.reviewSum + :rate,
            e.ratingAvg   = e.reviewSum * 1.0 / e.reviewCount
        where e.id = :exhibitionId
        """)
    void applyReviewCreated(@Param("exhibitionId") Long exhibitionId, @Param("rate")  int rate);

    @Modifying
    @Query("""
        update Exhibition e
        set e.reviewCount = e.reviewCount - 1,
            e.reviewSum = e.reviewSum - :rate,
            e.ratingAvg   = case
                              when e.reviewCount <= 0 then 0
                              else e.reviewSum * 1.0 / e.reviewCount
                             end
        where e.id =:exhibitionId and e.reviewCount > 0
        """)
    void applyReviewDeleted(@Param("exhibitionId") Long exhibitionId, @Param("rate")  int rate);

    // 삭제 x, 승인 o, 진행중이고, 리뷰수와 토탈리뷰점수를 더한 값이 높은 순으로 나열
    @Query("""
        SELECT e FROM Exhibition e
        WHERE e.isDeleted = false
          AND e.status = :status
          AND e.startDate <= :today AND e.endDate >= :today
          AND (e.exhibitionCategory = :exhibitionCategory OR e.exhibitionMood = :exhibitionMood)
        ORDER BY (e.reviewCount + e.reviewSum) DESC
        """)
    List<Exhibition> recommendByKeywords(
            @Param("exhibitionCategory") ExhibitionCategory exhibitionCategory,
            @Param("exhibitionMood") ExhibitionMood exhibitionMood,
            @Param("status") Status status,
            @Param("today") LocalDate today,
            org.springframework.data.domain.Pageable pageable
    );

    @Query("select count(e) from Exhibition e where e.status = :status")
    long findPendingExhibitionsCountByStatus(@Param("status") Status status);

    @Query("""
    select new com.project.team5backend.domain.admin.dashboard.dto.response.ExhibitionSummaryResDTO(
        e.title, u.name, e.address.district, e.createdAt
    )
    from Exhibition e
    join e.user u
    where e.status = :status
    order by e.createdAt desc
    """)
    List<ExhibitionSummaryResDTO> findTop3ByStatus(@Param("status") Status status);

    boolean existsByPortalExhibitionId(@Param("portalExhibitionId") Long portalExhibitionId);
}

