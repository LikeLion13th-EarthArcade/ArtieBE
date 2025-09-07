package com.project.team5backend.global.mail.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorCode implements BaseErrorCode {
    // ErrorCode
    TEMPLATE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "TEMPLATE500", "메일 전송 템플릿을 찾을 수 없습니다."),
    NOT_DEFINED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL500_1", "정의되지 않은 예외, 로그 확인"),
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "MAIL500_2", "메일 전송 실패"),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "MAIL400", "이메일 파싱 실패 또는 이메일 오류"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
