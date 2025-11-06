package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.admin.dashboard.dto.response.SpaceSummaryResDTO;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.common.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    Optional<Space> findByIdAndIsDeletedFalseAndStatusApproved(@Param("spaceId") Long spaceId,@Param("status") Status status);

    @Query("""
    select distinct s
    from Space s
    join fetch s.user
    where s.id = :spaceId
      and s.isDeleted = false
      and s.status = :status
""")
    Optional<Space> findByIdAndIsDeletedFalseAndStatusApprovedWithUser(@Param("spaceId") Long spaceId, @Param("status") Status status);

    @Query("""
    select distinct s
    from Space s
    join fetch s.user
    left join fetch s.spaceFacilities sf
    left join fetch sf.facility
    where s.id = :spaceId
      and s.isDeleted = false
      and s.status = :status
""")
    Optional<Space> findByIdAndIsDeletedFalseAndStatusApprovedWithUserAndFacilities(@Param("spaceId") Long spaceId, @Param("status") Status status);

    @Query("""
        select s
        from Space s
        where s.id =:spaceId
          and s.isDeleted = false
        """)
    Optional<Space> findByIdAndIsDeletedFalse(@Param("spaceId") Long spaceId);

    // 리뷰 평균/카운트 갱신
    @Modifying
    @Query("""
        update Space s
        set s.reviewCount = s.reviewCount + 1,
            s.reviewSum   = s.reviewSum + :rate,
            s.ratingAvg   = s.reviewSum * 1.0 / s.reviewCount
        where s.id =:spaceId
        """)
    void applyReviewCreated(@Param("spaceId") Long spaceId, @Param("rate")  int rate);

    @Modifying
    @Query("""
        update Space s
        set s.reviewCount = s.reviewCount - 1,
            s.reviewSum = s.reviewSum - :rate,
            s.ratingAvg   = case
                              when s.reviewCount <= 0 then 0
                              else s.reviewSum * 1.0 / s.reviewCount
                             end
        where s.id =:spaceId and s.reviewCount > 0
        """)
    void applySpaceReviewDeleted(@Param("spaceId") Long spaceId, @Param("rate")  int rate);

    @Query("select count(s) from Space s where s.status = :status")
    long findPendingSpacesCountByStatus(@Param("status") Status status);

    @Query("""
    select new com.project.team5backend.domain.admin.dashboard.dto.response.SpaceSummaryResDTO(
        s.name, u.name, s.address.district, s.createdAt
    )
    from Space s
    join s.user u
    where s.status = :status
    order by s.createdAt desc
    """)
    List<SpaceSummaryResDTO> findTop3ByStatus(@Param("status") Status status);

    Page<Space> findByIdIn(List<Long> ids, Pageable pageable);

    Page<Space> findByUser(User user, Pageable pageable);
}