package com.project.team5backend.global.entity.enums;


public enum Sort {
    NEW,      // 최신순
    OLD,      // 오래된 순
    POPULAR;  // 인기순

    public static Sort from(String s) {
        if (s == null || s.isBlank()) return NEW;
        return valueOf(s.toUpperCase());
    }
}
