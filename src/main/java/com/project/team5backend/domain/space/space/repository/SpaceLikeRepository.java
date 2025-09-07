package com.project.team5backend.domain.space.space.repository;

import com.project.team5backend.domain.space.space.entity.SpaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    Optional<SpaceLike> findBySpaceIdAndUserId(Long spaceId, Long userId);

    @Modifying
    @Query("delete from SpaceLike sl where sl.space.id =:spaceId")
    void deleteBySpaceId(@Param("spaceId") long spaceId);

}


