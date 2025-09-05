package com.project.team5backend.domain.user.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class UserException extends CustomException {
    public UserException(UserErrorCode errorCode) {
        super(errorCode);
    }
}
