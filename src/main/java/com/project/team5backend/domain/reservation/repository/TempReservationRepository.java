package com.project.team5backend.domain.reservation.repository;

import com.project.team5backend.domain.reservation.entity.TempReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempReservationRepository extends JpaRepository<TempReservation, Long> {
}
