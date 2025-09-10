package com.project.team5backend.domain.reservation.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class ReservationException extends CustomException {
    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode);
    }
}
