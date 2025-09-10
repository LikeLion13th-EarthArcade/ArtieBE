package com.project.team5backend.domain.reservation.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationErrorCode implements BaseErrorCode {
    // ErrorCode
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION404", "예약이 존재하지 않습니다."),
    RESERVATION_ACCESS_DENIED(HttpStatus.FORBIDDEN, "RESERVATION403", "예약에 대한 권한이 없습니다."),
    RESERVATION_STATUS_IS_NOT_APPROVABLE(HttpStatus.CONFLICT, "RESERVATION409_1", "예약이 수락할 수 있는 상태(PENDING, BOOKER_CANCEL_REQUESTED)가 아님"),
    RESERVATION_STATUS_IS_NOT_REJECTABLE(HttpStatus.CONFLICT, "RESERVATION409_2", "예약이 거절할 수 있는 상태(PENDING, BOOKER_CANCEL_REQUESTED, APPROVED)가 아님"),
    RESERVATION_STATUS_IS_NOT_CANCELLABLE(HttpStatus.CONFLICT, "RESERVATION409_3", "예약이 취소 요청할 수 있는 상태(PENDING, APPROVED)가 아님"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
