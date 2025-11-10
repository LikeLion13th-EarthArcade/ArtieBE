package com.project.team5backend.domain.reservation.repository;


import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r " +
            "FROM Reservation r " +
            "WHERE r.space.user = :user AND r.status = 'PENDING'")
    Page<Reservation> findBySpaceOwner(
            @Param("user") User user,
            Pageable pageable
    );

    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM Reservation r
        WHERE (r.startDate <= :date AND r.endDate >= :date)
    """)
    boolean existsByDateAndTimeSlots(@Param("date") LocalDate date);
}
