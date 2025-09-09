package com.project.team5backend.domain.space.review.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SpaceReviewErrorCode implements BaseErrorCode {
    SPACE_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "SPACE_REVIEW404_1", "공간 리뷰를 찾을 수 없습니다."),
    SPACE_REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "SPACE_REVIEW403_1", "해당 공간 리뷰에 대한 권한이 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
