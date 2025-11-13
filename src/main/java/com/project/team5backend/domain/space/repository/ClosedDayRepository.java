package com.project.team5backend.domain.space.repository;

import com.project.team5backend.domain.space.entity.ClosedDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClosedDayRepository extends JpaRepository<ClosedDay,Long> {
    List<ClosedDay> findBySpaceId(Long spaceId);
}
