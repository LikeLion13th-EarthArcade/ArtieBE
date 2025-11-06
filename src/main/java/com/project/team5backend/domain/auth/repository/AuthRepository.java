package com.project.team5backend.domain.auth.repository;

import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUser(User user);

    Optional<Auth> findByUserId(long id);

    @Query("SELECT a " +
            "FROM Auth a " +
            "WHERE a.user.email =:email")
    Optional<Auth> findByEmail(@Param("email") String email);
}
