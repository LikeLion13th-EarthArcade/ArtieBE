package com.project.team5backend.global.validation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.USER_CODE_PATTERN;

public class ValidationReqDTO {

    public record EmailCodeReqDTO(
            @NotBlank(message = USER_BLANK_EMAIL)
            @Email
            String email
    ) {
    }

    public record EmailCodeValidationReqDTO(
            @NotBlank(message = USER_BLANK_EMAIL)
            @Email
            String email,

            @NotBlank(message = USER_BLANK_CODE)
            @Pattern(regexp = USER_CODE_PATTERN, message = USER_WRONG_CODE)
            String code
    ) {
    }
}
