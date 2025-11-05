package com.project.team5backend.domain.reservation.dto.response;

import com.project.team5backend.domain.common.enums.Status;
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
            String hostCancelReason,
            String bookerCancelReason,
            Status status
    ) {
    }

    @Builder
    public record ReservationStatusResDTO(
            long spaceId,
            long reservationId,
            Status status
    ) {
    }
}
