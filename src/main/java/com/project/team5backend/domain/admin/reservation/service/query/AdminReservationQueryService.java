package com.project.team5backend.domain.admin.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.common.enums.StatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReservationQueryService {
    Page<ReservationResDTO.ReservationDetailResDTO> getReservationList(StatusGroup statusGroup, Pageable pageable);

    Page<ReservationResDTO.TempReservationDetailResDTO> getTempReservationList(Pageable pageable);

    ReservationResDTO.ReservationDetailResDTO getReservationDetail(Long reservationId);
}
