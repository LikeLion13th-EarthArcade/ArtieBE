package com.project.team5backend.domain.reservation.service.lock;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;

import java.time.LocalDate;
import java.util.List;

public interface DistributedLockService {

    ReservationResDTO.ReservationLockAcquireResDTO acquireLocks(String email, Long spaceId, ReservationReqDTO.ReservationLockAcquireReqDTO reservationLockAcquireReqDTO);

    long releaseLocks(Long spaceId, String email, List<LocalDate> dateSlots);
//
//    int renewLocks(Long spaceId, String email, List<LocalDate> timeSlots);
}
