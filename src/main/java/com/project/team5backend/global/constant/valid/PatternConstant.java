package com.project.team5backend.global.constant.valid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PatternConstant {

    // USER
    public static final String USER_PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^*()_+=\\-\\[\\],.?~])[A-Za-z\\d!@#$%^*()_+=\\-\\[\\],.?~]{8,16}$";
    public static final String USER_BIZ_NUMBER_PATTERN = "^\\d{10}$";

    public static final String USER_CODE_PATTERN = "^\\d{6}$";
}
