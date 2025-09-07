package com.project.team5backend.global.validation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import static com.project.team5backend.global.constant.valid.MessageConstant.USER_BLANK_EMAIL;

public class ValidationReqDTO {

    public record EmailCodeReqDTO(
            @NotBlank(message = USER_BLANK_EMAIL)
            @Email
            String email
    ) {
    }
}
