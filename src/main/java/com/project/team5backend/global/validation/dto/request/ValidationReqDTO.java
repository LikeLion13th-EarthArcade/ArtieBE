package com.project.team5backend.global.validation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.BIZ_NUMBER_PATTERN;
import static com.project.team5backend.global.constant.valid.PatternConstant.VALIDATION_CODE_PATTERN;

public class ValidationReqDTO {

    public record EmailCodeReqDTO(
            @NotBlank(message = BLANK_EMAIL)
            @Email
            String email
    ) {
    }

    public record EmailCodeValidationReqDTO(
            @NotBlank(message = BLANK_EMAIL)
            @Email
            String email,

            @NotBlank(message = BLANK_VALIDATION_CODE)
            @Pattern(regexp = VALIDATION_CODE_PATTERN, message = WRONG_VALIDATION_CODE_PATTERN)
            String code
    ) {
    }

    public record BizNumberValidationReqDTO(
            @NotBlank(message = BLANK_BIZ_NUMBER)
            @Pattern(regexp = BIZ_NUMBER_PATTERN, message = WRONG_BIZ_NUMBER_PATTERN)
            String bizNumber
    ) {
    }
}
