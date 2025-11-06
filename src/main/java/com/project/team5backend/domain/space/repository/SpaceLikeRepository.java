package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.space.entity.SpaceLike;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceLikeRepository extends JpaRepository<SpaceLike, Long> {
    boolean existsByUserIdAndSpaceId(long userId, long spaceId);

    @Modifying
    @Query("delete from SpaceLike sl where sl.user.id =:userId and sl.space.id =:spaceId")
    void deleteByUserIdAndSpaceId(@Param("userId") long userId, @Param("spaceId") long spaceId);

    @Modifying
    @Query("delete from SpaceLike sl where sl.space.id =:spaceId")
    void deleteBySpaceId(@Param("spaceId") long spaceId);

    @Query("select sl.space.id from SpaceLike sl where sl.user = :user")
    List<Long> findSpaceIdsByInterestedUser(@Param("user") User user);

}


