package com.project.team5backend.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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



