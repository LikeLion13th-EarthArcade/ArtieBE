package com.project.team5backend.global.constant.redis;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisConstant {

    // BLACK LIST
    public static final String KEY_BLACK_LIST_SUFFIX = ":blacklist";

    // JWT
    public static final String KEY_REFRESH_SUFFIX = ":refresh";


}
