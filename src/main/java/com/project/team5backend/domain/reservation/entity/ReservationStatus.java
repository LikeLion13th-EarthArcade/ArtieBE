package com.project.team5backend.domain.reservation.entity;

public enum ReservationStatus {
    NEW,        // 신규
    PENDING,    // 결제 완료 / 관리자 승인
    CONFIRMED,  // 호스트 승인
    CANCELLED,  // 호스트 거절, 예약자 거절, 관리자 거절
    DONE        // 예약이 정상 진행되어 끝난 상태
}
