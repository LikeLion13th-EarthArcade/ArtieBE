package com.project.team5backend.domain.admin.reservation.service.query;

import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.global.entity.enums.StatusGroup;
import com.project.team5backend.global.util.PageResponse;
import org.springframework.data.domain.Pageable;

public interface AdminReservationQueryService {
    PageResponse<ReservationResDTO.ReservationDetailResDTO> getReservationList(StatusGroup statusGroup, Pageable pageable);
}
