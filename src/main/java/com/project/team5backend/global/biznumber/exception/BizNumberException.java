package com.project.team5backend.global.biznumber.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class BizNumberException extends CustomException {
    public BizNumberException(BizNumberErrorCode errorCode) {
        super(errorCode);
    }
}
