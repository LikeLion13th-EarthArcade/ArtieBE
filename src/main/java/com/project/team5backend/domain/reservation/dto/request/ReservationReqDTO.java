package com.project.team5backend.domain.reservation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.PHONE_NUMBER_PATTERN;

public class ReservationReqDTO {

    public record ReservationCreateReqDTO(
            @NotNull(message = BLANK_RESERVATION_START_DATE)
            LocalDate startDate,

            @NotNull(message = BLANK_RESERVATION_END_DATE)
            LocalDate endDate,

            @NotBlank(message = BLANK_NAME)
            String name,

            @NotBlank(message = BLANK_PHONE_NUMBER)
            @Pattern(regexp = PHONE_NUMBER_PATTERN, message = WRONG_PHONE_NUMBER_PATTERN)
            String phoneNumber,

            @NotBlank(message = BLANK_EMAIL)
            @Email
            String email,

            String message
    ) {
    }
}
