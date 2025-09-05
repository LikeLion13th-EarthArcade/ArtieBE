package com.project.team5backend.domain.auth.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    // ErrorCode
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
