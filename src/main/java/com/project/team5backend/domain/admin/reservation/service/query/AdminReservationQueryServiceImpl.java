package com.project.team5backend.domain.admin.reservation.service.query;

import com.project.team5backend.domain.reservation.converter.ReservationConverter;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.exception.ReservationErrorCode;
import com.project.team5backend.domain.reservation.exception.ReservationException;
import com.project.team5backend.domain.reservation.repository.CustomReservationRepository;
import com.project.team5backend.domain.common.enums.StatusGroup;
import com.project.team5backend.domain.reservation.repository.ReservationRepository;
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
    private final ReservationRepository reservationRepository;

    @Override
    public Page<ReservationResDTO.ReservationDetailResDTO> getReservationList(StatusGroup statusGroup, Pageable pageable) {
        Page<Reservation> reservationPage = customReservationRepository.findAllReservationWithFilters(statusGroup, pageable);
        return reservationPage.map(ReservationConverter::toReservationDetailResDTO);
    }

    @Override
    public ReservationResDTO.ReservationDetailResDTO getReservationDetail(Long reservationId) {
        Reservation reservation = getReservation(reservationId);

        return ReservationConverter.toReservationDetailResDTO(reservation);
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.RESERVATION_NOT_FOUND));
    }
}
