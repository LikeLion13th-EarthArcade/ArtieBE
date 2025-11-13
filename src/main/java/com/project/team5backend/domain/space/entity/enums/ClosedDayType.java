package com.project.team5backend.domain.space.entity.enums;

public enum ClosedDayType {
    /**
     * 매주 반복되는 요일 휴무
     * ex) 매주 일요일
     */
    WEEKLY_RECURRING,

    /**
     * 매월 특정 날짜 휴무
     * ex) 매월 10일
     */
    MONTHLY_FIXED_DATE,

    /**
     * 매월 N번째 특정 요일 휴무
     * ex) 매월 둘째·넷째 토요일
     */
    MONTHLY_WEEK_PATTERN,

    /**
     * 특정일 (임시 휴무, 공휴일 등)
     * ex) 2025-12-25
     */
    SINGLE_DATE,

    /**
     * 특정 기간 전체 휴무
     * ex) 2025-08-01 ~ 2025-08-15
     */
    PERIOD_RANGE;    // 기간 휴무
}
