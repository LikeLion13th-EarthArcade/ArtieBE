package com.project.team5backend.domain.auth.repository;

import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByUser(User user);
}
