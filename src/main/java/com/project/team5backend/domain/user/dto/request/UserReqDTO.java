package com.project.team5backend.domain.user.dto.request;


import lombok.*;

@Getter @Setter
@NoArgsConstructor
public class UserReqDTO {

    public record UserCreateReqDTO(
            String email,
            String password,
            String name
    ) {
    }

    public record UserUpdateReqDTO(
            String name
    ) {
    }
}

