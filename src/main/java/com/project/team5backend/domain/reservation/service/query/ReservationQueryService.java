package com.project.team5backend.domain.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryService {

    ReservationResDTO.ReservationDetailResDTO getReservationDetail(long userId, long reservationId);

    Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(long userId, Pageable pageable);
}