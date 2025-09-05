package com.project.team5backend.global.security.dto;

import lombok.Builder;

@Builder
public record JwtDTO (
        String message,
        String accessToken,
        String refreshToken
) {
}