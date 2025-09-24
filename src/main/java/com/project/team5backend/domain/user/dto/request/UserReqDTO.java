package com.project.team5backend.domain.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.PASSWORD_PATTERN;

@Getter @Setter
@NoArgsConstructor
public class UserReqDTO {

    public record UserCreateReqDTO(
            @NotBlank(message = BLANK_EMAIL)
            @Email
            String email,

            @NotBlank(message = BLANK_PASSWORD)
            @Pattern(regexp = PASSWORD_PATTERN, message = WRONG_PASSWORD_PATTERN)
            String password,

            @NotBlank(message = BLANK_PASSWORD)
            @Pattern(regexp = PASSWORD_PATTERN, message = WRONG_PASSWORD_PATTERN)
            String passwordConfirmation,

            @NotBlank(message = BLANK_NAME)
            String name
    ) {
    }

    public record UserUpdateReqDTO(
            @NotBlank(message = BLANK_NAME)
            String name
    ) {
    }
}

