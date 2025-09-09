package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
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

}