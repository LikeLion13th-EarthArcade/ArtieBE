package com.project.team5backend.domain.user.converter;

import com.project.team5backend.domain.user.dto.request.UserReqDTO;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserConverter {

    public static User toUser(UserReqDTO.UserCreateReqDTO userCreateReqDTO) {
        return User.builder()
                .email(userCreateReqDTO.email())
                .name(userCreateReqDTO.name())
                .build();
    }
}