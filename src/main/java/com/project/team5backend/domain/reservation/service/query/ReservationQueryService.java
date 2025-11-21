package com.project.team5backend.domain.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryService {

    ReservationResDTO.ReservationDetailResDTO getReservationDetail(Long userId, Long reservationId);

    Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(Long userId, StatusGroup statusGroup, Pageable pageable);

    Page<ReservationResDTO.ReservationDetailResDTO> getMyReservationList(Long userId, StatusGroup statusGroup, Pageable pageable);

    Page<ReservationResDTO.TempReservationDetailResDTO> getMyTempReservation(Long userId, Pageable pageable);
}