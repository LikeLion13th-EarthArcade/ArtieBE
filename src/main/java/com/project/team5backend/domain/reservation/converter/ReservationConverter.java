package com.project.team5backend.domain.reservation.converter;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.enums.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReservationConverter {

    public static Reservation toReservation(Space space, User user, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO) {
        return Reservation.builder()
                .startDate(reservationCreateReqDTO.startDate())
                .endDate(reservationCreateReqDTO.endDate())
                .name(reservationCreateReqDTO.name())
                .email(reservationCreateReqDTO.email())
                .phoneNumber(reservationCreateReqDTO.phoneNumber())
                .message(reservationCreateReqDTO.message())
                .status(Status.PENDING)
                .space(space)
                .user(user)
                .build();
    }

    public static ReservationResDTO.ReservationCreateResDTO toReservationCreateResDTO(Reservation reservation) {
        return ReservationResDTO.ReservationCreateResDTO.builder()
                .id(reservation.getId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .build();
    }
}
