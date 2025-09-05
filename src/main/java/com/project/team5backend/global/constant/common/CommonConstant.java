package com.project.team5backend.global.constant.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonConstant {

    // COOKIE NAME
    public static final String ACCESS_COOKIE_NAME = "access-token";
    public static final String REFRESH_COOKIE_NAME = "refresh-token";
    public static final String CSRF_COOKIE_NAME = "XSRF-TOKEN";

    // CSRF EXP TIME
    public static final int CSRF_COOKIE_MAX_AGE = -1;
}
