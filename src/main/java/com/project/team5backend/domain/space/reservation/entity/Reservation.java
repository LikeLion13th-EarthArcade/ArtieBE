package com.project.team5backend.domain.space.reservation.entity;


import com.project.team5backend.domain.space.space.entity.Space;
import com.project.team5backend.domain.user.entity.User;
import com.project.team5backend.global.entity.BaseTimeEntity;
import com.project.team5backend.global.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "reservation")
public class Reservation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String name;

    private String email;

    private String phoneNumber;

    private String message;

    private String cancelReason;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 예약자

    // 예약 확정
    public void confirm() {
        this.status = Status.APPROVED;
    }
    // 예약 취소
    public void cancel(String reason) {
        this.status = Status.REJECTED;
        this.cancelReason = reason;
    }
}


