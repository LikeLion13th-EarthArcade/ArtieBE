package com.project.team5backend.domain.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.global.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryService {

    ReservationResDTO.ReservationDetailResDTO getReservationDetail(long userId, long reservationId);

    Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(long userId, Pageable pageable);
    Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(long userId, ReservationStatus status, Pageable pageable);

}