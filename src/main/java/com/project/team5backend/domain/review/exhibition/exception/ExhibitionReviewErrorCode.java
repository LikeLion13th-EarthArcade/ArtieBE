package com.project.team5backend.domain.review.exhibition.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionReviewErrorCode implements BaseErrorCode {
    EXHIBITION_REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "EX_REVIEW404_1", "전시리뷰를 찾을 수 없습니다."),
    EXHIBITION_REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "EX_REVIEW403_1", "해당 전시리뷰에 대한 권한이 없습니다."),
    EXHIBITION_REVIEW_BAD_REQUEST(HttpStatus.BAD_REQUEST, "EX_REVIEW400_1", "목록 요청시, ReviewSearchType이 Space 입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
