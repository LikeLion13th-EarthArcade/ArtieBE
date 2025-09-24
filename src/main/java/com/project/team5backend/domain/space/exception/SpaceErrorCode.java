package com.project.team5backend.domain.space.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SpaceErrorCode implements BaseErrorCode {
    SPACE_FORBIDDEN(HttpStatus.FORBIDDEN, "SPACE403_1", "해당 공간에 대한 권한이 없습니다."),
    APPROVED_SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_404", "승인된 전시 공간을 찾을 수 없습니다."),
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_404_1", "전시 공간을 찾을 수 없습니다."),
    BIZ_NUMBER_VALIDATION_DOES_NOT_EXIST(HttpStatus.UNPROCESSABLE_ENTITY, "SPACE422", "사업자 번호 검증을 시도하지 않았거나 변조되었거나 만료되었습니다."),
    SPACE_VERIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_404_1", "전시 공간을 찾을 수 없습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
