package com.project.team5backend.domain.reservation.dto.response;

import com.project.team5backend.domain.reservation.entity.ReservationStatus;
import com.project.team5backend.global.entity.enums.Status;
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

    @Builder
    public record ReservationDetailResDTO(
            long spaceId,
            long reservationId,
            LocalDate startDate,
            LocalDate endDate,
            String name,
            String email,
            String phoneNumber,
            String message,
            String cancelReason,
            Status status
    ) {
    }
}
