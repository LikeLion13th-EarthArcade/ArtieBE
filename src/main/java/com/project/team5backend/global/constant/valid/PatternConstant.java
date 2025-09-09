package com.project.team5backend.global.constant.valid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatternConstant {

    // USER
    public static final String PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^*()_+=\\-\\[\\],.?~])[A-Za-z\\d!@#$%^*()_+=\\-\\[\\],.?~]{8,16}$";
    public static final String BIZ_NUMBER_PATTERN = "^\\d{10}$";

    public static final String VALIDATION_CODE_PATTERN = "^\\d{6}$";

    // RESERVATION
    public static final String PHONE_NUMBER_PATTERN = "^\\d{11}$";
    public static final String RESERVATION_DATE_PATTERN = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$";
}
