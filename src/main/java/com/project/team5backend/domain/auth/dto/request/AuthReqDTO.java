package com.project.team5backend.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.PASSWORD_PATTERN;

public class AuthReqDTO {

    public record AuthLoginReqDTO(
            String email,
            String password
    ) {
    }

    public record AuthPasswordChangeReqDTO(
            @NotBlank
            @Pattern(regexp = PASSWORD_PATTERN, message = WRONG_PASSWORD_PATTERN)
            String currentPassword,

            @NotBlank
            @Pattern(regexp = PASSWORD_PATTERN, message = WRONG_PASSWORD_PATTERN)
            String newPassword,

            @NotBlank
            @Pattern(regexp = PASSWORD_PATTERN, message = WRONG_PASSWORD_PATTERN)
            String newPasswordConfirmation
    ) {
    }

    public record AuthTempPasswordReqDTO(
            @NotBlank(message = BLANK_PHONE_NUMBER)
            @Email
            String email
    ) {
    }
}
