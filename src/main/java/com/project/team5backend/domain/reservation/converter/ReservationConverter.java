package com.project.team5backend.domain.reservation.converter;

import com.project.team5backend.domain.reservation.dto.request.ReservationReqDTO;
import com.project.team5backend.domain.reservation.dto.response.ReservationResDTO;
import com.project.team5backend.domain.reservation.entity.Reservation;
import com.project.team5backend.domain.reservation.entity.TempReservation;
import com.project.team5backend.domain.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.domain.common.enums.Status;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

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

    public static Reservation toReservation(TempReservation tempReservation) {
        return Reservation.builder()
                .startDate(tempReservation.getStartDate())
                .endDate(tempReservation.getEndDate())
                .name(tempReservation.getName())
                .email(tempReservation.getEmail())
                .phoneNumber(tempReservation.getPhoneNumber())
                .message(tempReservation.getMessage())
                .status(Status.PENDING)
                .space(tempReservation.getSpace())
                .user(tempReservation.getUser())
                .build();
    }

    public static ReservationResDTO.ReservationCreateResDTO toReservationCreateResDTO(Reservation reservation) {
        return ReservationResDTO.ReservationCreateResDTO.builder()
                .id(reservation.getId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .build();
    }

    public static ReservationResDTO.ReservationDetailResDTO toReservationDetailResDTO(Reservation reservation) {
        return ReservationResDTO.ReservationDetailResDTO.builder()
                .spaceId(reservation.getSpace().getId())
                .reservationId(reservation.getId())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .name(reservation.getName())
                .email(reservation.getEmail())
                .phoneNumber(reservation.getPhoneNumber())
                .message(reservation.getMessage())
                .hostCancelReason(reservation.getHostCancelReason())
                .bookerCancelReason(reservation.getBookerCancelReason())
                .status(reservation.getStatus())
                .build();
    }

    public static ReservationResDTO.TempReservationDetailResDTO toTempReservationDetailResDTO(TempReservation tempReservation) {
        return ReservationResDTO.TempReservationDetailResDTO.builder()
                .spaceId(tempReservation.getSpace().getId())
                .tempReservationId(tempReservation.getTempReservationId())
                .startDate(tempReservation.getStartDate())
                .endDate(tempReservation.getEndDate())
                .name(tempReservation.getName())
                .email(tempReservation.getEmail())
                .phoneNumber(tempReservation.getPhoneNumber())
                .message(tempReservation.getMessage())
                .accountNumber("계좌번호 추가 예정")
                .isDeposited(tempReservation.isDeposited())
                .build();

    }

    public static ReservationResDTO.ReservationStatusResDTO toReservationStatusResDTO(Reservation reservation) {
        return ReservationResDTO.ReservationStatusResDTO.builder()
                .spaceId(reservation.getSpace().getId())
                .reservationId(reservation.getId())
                .status(reservation.getStatus())
                .build();
    }

    public static ReservationResDTO.ReservationLockAcquireResDTO toReservationLockAcquireResDTO(Long spaceId, List<Object> result) {
        return ReservationResDTO.ReservationLockAcquireResDTO.builder()
                .spaceId(spaceId)
//                .allLocked(isAllLocked(result.get(0)))
                .result(result.get(1))
                .build();
    }

    public static ReservationResDTO.ReservationLockReleaseResDTO toReservationLockReleaseResDTO(Long spaceId, Long count) {
        return ReservationResDTO.ReservationLockReleaseResDTO.builder()
                .spaceId(spaceId)
                .count(count)
                .build();
    }

    public static TempReservation toTempReservation (Space space, User user, ReservationReqDTO.ReservationCreateReqDTO reservationCreateReqDTO) {
        return TempReservation.builder()
                .startDate(reservationCreateReqDTO.startDate())
                .endDate(reservationCreateReqDTO.endDate())
                .name(reservationCreateReqDTO.name())
                .email(reservationCreateReqDTO.email())
                .phoneNumber(reservationCreateReqDTO.phoneNumber())
                .message(reservationCreateReqDTO.message())
                .isDeposited(false)
                .space(space)
                .user(user)
                .build();
    }

    public static ReservationResDTO.ReservationCreateResDTO toReservationCreateReqDTO(TempReservation tempReservation) {
        return ReservationResDTO.ReservationCreateResDTO.builder()
                .id(tempReservation.getTempReservationId())
                .startDate(tempReservation.getStartDate())
                .endDate(tempReservation.getEndDate())
                .build();

    }

//    private static boolean isAllLocked(Object result) {
//        return Objects.equals(result, 1L);
//    }
}
