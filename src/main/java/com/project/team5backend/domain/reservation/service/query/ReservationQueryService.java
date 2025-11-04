package com.project.team5backend.domain.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservationQueryService {

    ReservationResDTO.ReservationDetailResDTO getReservationDetail(long userId, long reservationId);

    Page<ReservationResDTO.ReservationDetailResDTO> getReservationListForSpaceOwner(long userId, StatusGroup statusGroup, Pageable pageable);

    Page<ReservationResDTO.ReservationDetailResDTO> getMyReservationList(long userId,  StatusGroup statusGroup, Pageable pageable);
}