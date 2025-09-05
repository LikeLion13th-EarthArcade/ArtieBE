package com.project.team5backend.domain.auth.dto.request;

public class AuthReqDTO {

    public record AuthLoginReqDTO(
            String email,
            String password
    ) {
    }
}
