package com.project.team5backend.domain.user.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
public class UserResDTO {

    @Builder
    public record UserProfileResDTO(
            String name,
            String email
    ) {
    }
}



