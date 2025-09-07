package com.project.team5backend.global.validation.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ValidationErrorCode implements BaseErrorCode {
    // ErrorCode
    ALREADY_USED_EMAIL(HttpStatus.CONFLICT, "VALID409", "이미 사용중인 이메일입니다."),
    CODE_COOL_DOWN(HttpStatus.BAD_GATEWAY, "MSG502_2", "잠시 후 다시 시도해주세요. 메일은 10초에 한 번만 보낼 수 있습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
