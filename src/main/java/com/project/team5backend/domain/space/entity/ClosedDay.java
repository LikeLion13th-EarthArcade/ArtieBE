package com.project.team5backend.domain.space.entity;

import com.project.team5backend.domain.space.entity.enums.ClosedDayType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ClosedDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long closedDayId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClosedDayType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    private Space space;

    /**
     * 요일 (0=일요일, 6=토요일)
     * - WEEKLY_RECURRING
     * - MONTHLY_NTH_WEEKDAY
     */
    private Integer dayOfWeek;

    /**
     * 매월 특정 날짜
     * - MONTHLY_FIXED_DATE
     */
    private Integer date;

    /**
     * 몇째 주 (1~5)
     * - MONTHLY_NTH_WEEKDAY
     */
    private Integer weekOfMonth;

    /**
     * 단일 특정일 휴무
     * - ONE_TIME_DATE
     */
    private LocalDate specificDate;

    /**
     * 기간 휴무 시작/종료일
     * - PERIOD_RANGE
     */
    private LocalDate startDate;
    private LocalDate endDate;

    // == 비즈니스 로직 ==
    public boolean isClosedOn(LocalDate targetDate) {
        if (type == null) return false;

        switch (this.type) {
            // 매주 반복 휴무 (예: 매주 일요일)
            case WEEKLY_RECURRING -> {
                if (this.dayOfWeek == null) return false;
                int targetDay = targetDate.getDayOfWeek().getValue() % 7;
                return targetDay == this.dayOfWeek;
            }

            // 매월 특정 날짜 (예: 매월 10일)
            case MONTHLY_FIXED_DATE -> {
                if (this.date == null) return false;
                return targetDate.getDayOfMonth() == this.date;
            }

            // 매월 N번째 특정 요일 (예: 매월 둘째 토요일)
            case MONTHLY_WEEK_PATTERN -> {
                if (this.dayOfWeek == null || this.weekOfMonth == null) return false;

                int targetDay = targetDate.getDayOfWeek().getValue() % 7;
                int targetWeek = (targetDate.getDayOfMonth() - 1) / 7 + 1;

                return targetDay == this.dayOfWeek && targetWeek == this.weekOfMonth;
            }

            // 특정일 (임시 휴무, 공휴일 등)
            case SINGLE_DATE -> {
                if (this.specificDate == null) return false;
                return targetDate.equals(this.specificDate);
            }

            // 특정 기간 전체 휴무
            case PERIOD_RANGE -> {
                if (this.startDate == null || this.endDate == null) return false;
                return !targetDate.isBefore(this.startDate)
                        && !targetDate.isAfter(this.endDate);
            }

            default -> {
                return false;
            }
        }
    }
}
