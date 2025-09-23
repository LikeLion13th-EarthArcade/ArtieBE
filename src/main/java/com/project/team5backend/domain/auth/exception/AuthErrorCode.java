package com.project.team5backend.domain.auth.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    // ErrorCode
    AUTH_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH404", "AUTH 정보가 존재하지 않습니다."),
    NEW_PASSWORD_DOES_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASS400_1", "새 비밀번호와 비밀번호 재입력이 일치하지 않습니다."),
    CURRENT_PASSWORD_DOES_NOT_MATCH(HttpStatus.BAD_REQUEST, "PASS400_2", "현재 비밀번호가 일치하지 않습니다."),
    NEW_PASSWORD_IS_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "PASS400_3", "현재 비빌번호와 새 비밀번호가 일치합니다."),
    EMAIL_VALIDATION_DOES_NOT_EXIST(HttpStatus.UNAUTHORIZED, "AUTH401", "임시 비밀번호 발급 이메일 인증을 시도하지 않았거나 변조되었거나 만료되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
