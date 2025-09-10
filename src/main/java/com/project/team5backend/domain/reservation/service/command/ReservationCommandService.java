package com.project.team5backend.domain.reservation.service.command;


import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;

public interface ReservationCommandService {

    ReservationResDTO.ReservationCreateResDTO createReservation(long spaceId, long userId, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO);
}