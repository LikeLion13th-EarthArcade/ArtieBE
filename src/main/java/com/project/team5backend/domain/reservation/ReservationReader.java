package com.project.team5backend.domain.reservation;

import com.project.team5backend.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReservationReader {

    private final ReservationRepository reservationRepository;

    public boolean existsOnDate(LocalDate date) {
        return reservationRepository.existsByDateAndTimeSlots(date);
    }
}
