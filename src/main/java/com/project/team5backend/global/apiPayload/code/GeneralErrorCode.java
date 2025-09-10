package com.project.team5backend.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GeneralErrorCode implements BaseErrorCode {

    BAD_REQUEST_400(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다"),

    UNAUTHORIZED_401(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다"),

    FORBIDDEN_403(HttpStatus.FORBIDDEN, "COMMON403", "접근이 금지되었습니다"),

    NOT_FOUND_404(HttpStatus.NOT_FOUND, "COMMON404", "요청한 자원을 찾을 수 없습니다"),

    INTERNAL_SERVER_ERROR_500(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 내부 오류가 발생했습니다"),

    // 유효성 검사
    VALIDATION_FAILED_PARAM(HttpStatus.BAD_REQUEST, "VALID400_0", "잘못된 파라미터 입니다."),

    VALIDATION_FAILED_DTO_FILED(HttpStatus.BAD_REQUEST, "VALID400_1", "잘못된 필드 입력입니다."),

    INVALID_JSON_SYNTAX(HttpStatus.BAD_REQUEST, "HTTP400_1", "요청 본문의 JSON 문법이 잘못됨 / e : JsonParseException"),
    INVALID_FIELD_FORMAT(HttpStatus.BAD_REQUEST, "HTTP400_2","필드 값의 형식이 올바르지 않음 / e : InvalidFormatException"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "HTTP400_3","요청 본문의 형식이 올바르지 않음 / e : MismatchedInputException"),
    BAD_REQUEST_BODY(HttpStatus.BAD_REQUEST, "HTTP400_4", "요청 본문을 읽을 수 없음 JSON 작성 오류일 가능성이 높을겁니다 아마도요 (정의되지 않은 400)"),

    INVALID_LOCAL_DATE(HttpStatus.BAD_REQUEST, "LOCAL_DATE400", "YYYY-MM-DD 형식의 유효한 날짜만 허용"),
    INVALID_ENUM(HttpStatus.BAD_REQUEST, "ENUM400", "ENUM 입력 오류"),
    ;

    // 필요한 필드값 선언
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
