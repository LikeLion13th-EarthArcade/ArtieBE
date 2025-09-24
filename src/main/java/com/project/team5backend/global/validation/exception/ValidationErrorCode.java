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
    CODE_COOL_DOWN(HttpStatus.BAD_GATEWAY, "VALID502", "잠시 후 다시 시도해주세요. 메일은 10초에 한 번만 보낼 수 있습니다."),
    VALIDATION_REQUEST_DOES_NOT_EXIST(HttpStatus.UNPROCESSABLE_ENTITY, "VALID422", "해당 이메일과 관련된 인증이 없거나 인증 코드가 유효하지 않습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "VALID404", "해당 이메일로 가입된 계정이 존재하지 않습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
