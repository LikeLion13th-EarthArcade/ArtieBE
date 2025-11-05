package com.project.team5backend.domain.common.enums;

public enum Status {
    PENDING,    // 입금 완료된 상태, 호스트 검색가능      & 전시/공간 심사중
    APPROVED,   // 호스트의 예약 확정                   & 관리자의 전시/공간 승인
    REJECTED,   // 거절

    CANCELED, // PENDING 상태에서 예약자의 취소

    BOOKER_CANCEL_REQUESTED, // 예약자의 취소 요청
    CANCELED_BY_BOOKER,    // 예약자의 요청으로 취소 완료
    BOOKER_CANCEL_REJECTED, // 예약자의 취소 요청 거절

    CANCELED_BY_HOST, // 호스트의 취소

    ;
}
