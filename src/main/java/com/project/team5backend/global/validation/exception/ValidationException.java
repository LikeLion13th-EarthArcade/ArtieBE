package com.project.team5backend.global.validation.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class ValidationException extends CustomException {
    public ValidationException(ValidationErrorCode errorCode) {
        super(errorCode);
    }
}
