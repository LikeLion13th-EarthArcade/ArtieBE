package com.project.team5backend.domain.facility.repository;

import com.project.team5backend.domain.facility.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    List<Facility> findByNameIn(List<String> names);
}
