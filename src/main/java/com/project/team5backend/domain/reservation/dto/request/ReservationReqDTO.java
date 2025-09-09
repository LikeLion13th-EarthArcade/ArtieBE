package com.project.team5backend.domain.reservation.dto.request;

import java.time.LocalDate;

public class ReservationReqDTO {

    public record ReservationCreateReqDTO(
            LocalDate startDate,
            LocalDate endDate,
            String name,
            String phoneNumber,
            String email,
            String message
    ) {
    }
}
