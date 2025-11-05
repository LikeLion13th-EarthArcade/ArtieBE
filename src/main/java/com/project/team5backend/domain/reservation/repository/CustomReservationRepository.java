package com.project.team5backend.domain.reservation.repository;

import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReservationRepository {

    Page<Reservation> findBySpaceOwnerWithFilters(User user, StatusGroup statusGroup, Pageable pageable);

    Page<Reservation> findByUserWithFilters(User user, StatusGroup statusGroup, Pageable pageable);

    Page<Reservation> findAllReservationWithFilters(StatusGroup statusGroup, Pageable pageable);
}