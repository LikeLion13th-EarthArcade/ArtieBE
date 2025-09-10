package com.project.team5backend.global.entity.enums;

public enum Status {
    NEW,        // 예약 생성 예약금 미입금
    PENDING,    // 입금 완료된 상태, 호스트 검색가능      & 전시/공간 심사중
    APPROVED,   // 호스트의 예약 확정                   & 관리자의 전시/공간 승인
    REJECTED,   // 거절
    CANCELED
    ;
}
