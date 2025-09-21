package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.space.entity.SpaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    boolean existsByUserIdAndSpaceId(long spaceId, long userId);

    @Modifying
    @Query("delete from SpaceLike sl where sl.user.id =:userId and sl.space.id =:spaceId")
    void deleteByUserIdAndSpaceId(@Param("spaceId") long spaceId,@Param("userId") long userId);

    @Modifying
    @Query("delete from SpaceLike sl where sl.space.id =:spaceId")
    void deleteBySpaceId(@Param("spaceId") long spaceId);

}


