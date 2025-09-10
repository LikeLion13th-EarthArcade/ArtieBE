package com.project.team5backend.domain.reservation.repository;

import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReservationRepository {

    Page<Reservation> findBySpaceOwnerWithFilters(User user, ReservationStatus status, Pageable pageable);
}