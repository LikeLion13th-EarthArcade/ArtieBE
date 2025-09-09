package com.project.team5backend.domain.space.space.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SpaceErrorCode implements BaseErrorCode {
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE_404", "승인된 전시 공간을 찾을 수 없습니다."),
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
