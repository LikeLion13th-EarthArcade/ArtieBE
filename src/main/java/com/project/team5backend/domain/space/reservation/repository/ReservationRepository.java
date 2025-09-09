package com.project.team5backend.domain.space.reservation.repository;


import com.project.team5backend.domain.space.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
