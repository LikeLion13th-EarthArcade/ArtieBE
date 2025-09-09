package com.project.team5backend.domain.reservation.dto.response;

import lombok.Builder;

import java.time.LocalDate;

public class ReservationResDTO {

    @Builder
    public record ReservationCreateResDTO(
            long id,
            LocalDate startDate,
            LocalDate endDate
    ) {
    }
}
