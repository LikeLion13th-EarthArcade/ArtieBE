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
     * 몇째 주인지 (예: [2,4] → 둘째, 넷째)
     * - MONTHLY_NTH_WEEKDAY
     */
//    @ElementCollection
//    @CollectionTable(name = "closed_day_weeks", joinColumns = @JoinColumn(name = "closed_day_id"))
//    @Column(name = "week_of_month")
    private List<Integer> weekOfMonth;

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
        switch (this.type) {
            case WEEKLY_RECURRING -> {
                return targetDate.getDayOfWeek().getValue() % 7 == this.dayOfWeek;
            }
            case MONTHLY_FIXED_DATE -> {
                return targetDate.getDayOfMonth() == this.date;
            }
            case MONTHLY_WEEK_PATTERN -> {
                int weekIndex = (targetDate.getDayOfMonth() - 1) / 7 + 1;
                return this.dayOfWeek != null
                        && targetDate.getDayOfWeek().getValue() % 7 == this.dayOfWeek
                        && this.weekOfMonth != null
                        && this.weekOfMonth.contains(weekIndex);
            }
            case SINGLE_DATE -> {
                return targetDate.equals(this.specificDate);
            }
            case PERIOD_RANGE -> {
                return !targetDate.isBefore(this.startDate)
                        && !targetDate.isAfter(this.endDate);
            }
        }
        return false;
    }
}
