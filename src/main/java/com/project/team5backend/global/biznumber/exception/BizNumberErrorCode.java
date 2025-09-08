package com.project.team5backend.global.biznumber.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BizNumberErrorCode implements BaseErrorCode {
    // ErrorCode
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "BIZ502", "국세청 API 연동 실패"),
    EXTERNAL_API_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "BIZ503", "국세청 API 응답 지연"),
    PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "BIZ500","국세청 API 응답 처리 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
