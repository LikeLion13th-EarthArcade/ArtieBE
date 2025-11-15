package com.project.team5backend.domain.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static List<LocalDate> generateSlots(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateSlots = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            dateSlots.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return dateSlots;
    }
}
