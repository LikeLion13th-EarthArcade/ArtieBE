package com.project.team5backend.domain.user.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import static com.project.team5backend.global.constant.valid.MessageConstant.*;
import static com.project.team5backend.global.constant.valid.PatternConstant.USER_PASSWORD_PATTERN;

@Getter @Setter
@NoArgsConstructor
public class UserReqDTO {

    public record UserCreateReqDTO(
            @NotBlank(message = USER_BLANK_EMAIL)
            @Email
            String email,

            @NotBlank(message = USER_BLANK_PASSWORD)
            @Pattern(regexp = USER_PASSWORD_PATTERN, message = USER_WRONG_PASSWORD)
            String password,

            @NotBlank(message = USER_BLANK_PASSWORD)
            @Pattern(regexp = USER_PASSWORD_PATTERN, message = USER_WRONG_PASSWORD)
            String passwordConfirmation,

            @NotBlank(message = USER_BLANK_NAME)
            String name
    ) {
    }

    public record UserUpdateReqDTO(
            @NotBlank(message = USER_BLANK_NAME)
            String name
    ) {
    }
}

