package com.project.team5backend.domain.user.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    // ErrorCode
    EMAIL_DUPLICATED(HttpStatus.CONFLICT, "USER409", "이미 가입된 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "유저가 존재하지 않습니다"),
    SIGN_UP_EMAIL_VALIDATION_DOES_NOT_EXIST(HttpStatus.UNAUTHORIZED, "USER401", "회원 가입 이메일 인증을 시도하지 않았거나 변조되었거나 만료되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
