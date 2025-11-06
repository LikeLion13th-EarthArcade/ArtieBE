package com.project.team5backend.domain.reservation.service.command;


import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;

public interface ReservationCommandService {

    ReservationResDTO.ReservationCreateResDTO createReservation(Long spaceId, Long userId, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO);

    ReservationResDTO.ReservationStatusResDTO approveRequest(Long userId, Long reservationId);

    ReservationResDTO.ReservationStatusResDTO rejectRequest(Long userId, Long reservationId, ReservationReqDTO.ReservationRejectReqDTO reservationRejectReqDTO);

    ReservationResDTO.ReservationStatusResDTO requestCancellation(Long userId, Long reservationId, ReservationReqDTO.ReservationCancellationReqDTO reservationCancellationReqDTO);
}