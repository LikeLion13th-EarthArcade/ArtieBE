package com.project.team5backend.global.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstant {
    // --------KEY--------

    // PHONE NUMBER VALIDATION
    public static final String KEY_CODE_SUFFIX = ":code";
    public static final String KEY_COOLDOWN_SUFFIX = ":cooldown";

    // BLACK LIST
    public static final String KEY_BLACK_LIST_SUFFIX = ":blacklist";

    // JWT
    public static final String KEY_REFRESH_SUFFIX = ":refresh";

    // -------VALUE-------

    // PHONE NUMBER VALIDATION
    public static final String VALUE_COOLDOWN = "cooldown...";

    // -------TIME--------

    // PHONE NUMBER VALIDATION
    public static final long CODE_EXP_TIME = 300000L;
    public static final long COOLDOWN_EXP_TIME = 10000L;


}
