package com.project.team5backend.domain.admin.reservation.service.query;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.repository.CustomReservationRepository;
import com.project.team5backend.global.entity.enums.StatusGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminReservationQueryServiceImpl implements  AdminReservationQueryService {

    private final CustomReservationRepository customReservationRepository;

    @Override
    public Page<ReservationResDTO.ReservationDetailResDTO> getReservationList(StatusGroup statusGroup, Pageable pageable) {
        Page<Reservation> reservationPage = customReservationRepository.findAllReservationWithFilters(statusGroup, pageable);
        return reservationPage.map(ReservationConverter::toReservationDetailResDTO);
    }
}
