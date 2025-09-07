package com.project.team5backend.global.mail.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class MailException extends CustomException {
    public MailException(MailErrorCode errorCode) {
        super(errorCode);
    }
}
