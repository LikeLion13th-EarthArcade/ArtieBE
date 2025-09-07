package com.project.team5backend.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.project.team5backend.global.constant.valid.MessageConstant.USER_WRONG_PASSWORD;
import static com.project.team5backend.global.constant.valid.PatternConstant.USER_PASSWORD_PATTERN;

public class AuthReqDTO {

    public record AuthLoginReqDTO(
            String email,
            String password
    ) {
    }

    public record AuthPasswordChangeReqDTO(
            @NotBlank
            @Pattern(regexp = USER_PASSWORD_PATTERN, message = USER_WRONG_PASSWORD)
            String currentPassword,

            @NotBlank
            @Pattern(regexp = USER_PASSWORD_PATTERN, message = USER_WRONG_PASSWORD)
            String newPassword,

            @NotBlank
            @Pattern(regexp = USER_PASSWORD_PATTERN, message = USER_WRONG_PASSWORD)
            String newPasswordConfirmation
    ) {
    }
}
