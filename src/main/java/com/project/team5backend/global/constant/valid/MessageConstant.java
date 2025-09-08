package com.project.team5backend.global.constant.valid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageConstant {

    // NOT BLANK
    public static final String USER_BLANK_EMAIL = "이메일 누락";
    public static final String USER_BLANK_NAME = "이름 누락";
    public static final String USER_BLANK_PASSWORD = "비밀번호 누락";
    public static final String USER_BLANK_CODE = "인증 코드 누락";
    public static final String USER_BLANK_BIZ_NUMBER = "사업자 번호 누락";

    // PATTERN EXCEPTION
    public static final String USER_WRONG_PASSWORD = "공백없는 8-16자리의 대/소문자+숫자+특수문자만 허용, 사용불가 특수문자 : <, >, {, }, |, ;, ', \"";
    public static final String USER_WRONG_BIZ_NUMBER = "공백없는 숫자 10자리만 허용";

    public static final String USER_WRONG_CODE = "공백없는 숫자 6자리만 허용";

    // BIZ NUMBER API MESSAGE
    public static final String BIZ_NUMBER_IS_NOT_REGISTERED = "국세청에 등록되지 않은 사업자등록번호입니다.";
    public static final String BLANK = "";

    // PHONE NUMBER CONFIRMATION RESULT MESSAGE
    public static final String CODE_CONFIRMATION_IS_SUCCESS = "인증 번호 검증 성공!";
    public static final String CODE_CONFIRMATION_IS_FAILURE = "인증 번호 검증 실패";
}
