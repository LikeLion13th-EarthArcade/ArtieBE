package com.project.team5backend.global.constant.valid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstant {

    // NOT BLANK
    public static final String BLANK_EMAIL = "이메일 누락";
    public static final String BLANK_NAME = "이름 누락";
    public static final String BLANK_PASSWORD = "비밀번호 누락";
    public static final String BLANK_VALIDATION_CODE = "인증 코드 누락";
    public static final String BLANK_BIZ_NUMBER = "사업자 번호 누락";
    public static final String BLANK_RESERVATION_START_DATE = "예약 시작 시간 누락";
    public static final String BLANK_RESERVATION_END_DATE = "예약 종료 시간 누락";
    public static final String BLANK_PHONE_NUMBER = "전화 번호 누락";
    public static final String BLANK_RESERVATION_CANCEL_REASON = "취소 사유 누락";


    // PATTERN EXCEPTION
    public static final String WRONG_PASSWORD_PATTERN = "공백없는 8-16자리의 대/소문자+숫자+특수문자만 허용, 사용불가 특수문자 : <, >, {, }, |, ;, ', \"";
    public static final String WRONG_BIZ_NUMBER_PATTERN = "공백없는 숫자 10자리만 허용";
    public static final String WRONG_VALIDATION_CODE_PATTERN = "공백없는 숫자 6자리만 허용";
    public static final String WRONG_PHONE_NUMBER_PATTERN = "공백없는 숫자 11자리만 허용";
    public static final String WRONG_RESERVATION_DATE_PATTERN = "YYYY-MM-DD 형식의 유효한 날짜만 허용";

    // BIZ NUMBER API MESSAGE
    public static final String BIZ_NUMBER_IS_NOT_REGISTERED = "국세청에 등록되지 않은 사업자등록번호입니다.";
    public static final String BLANK = "";

    // PHONE NUMBER CONFIRMATION RESULT MESSAGE
    public static final String CODE_CONFIRMATION_IS_SUCCESS = "인증 번호 검증 성공!";
    public static final String CODE_CONFIRMATION_IS_FAILURE = "인증 번호 검증 실패";
}
