package com.project.team5backend.domain.reservation.repository;


import com.project.team5backend.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END
        FROM Reservation r
        WHERE (r.startDate <= :date AND r.endDate >= :date)
    """)
    boolean existsByDateAndTimeSlots(@Param("date") LocalDate date);
}
