package com.project.team5backend.domain.auth.converter;

import com.project.team5backend.domain.auth.entity.Auth;
import com.project.team5backend.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthConverter {

    public static Auth toAuth(User user, String encodedPassword) {
        return Auth.builder()
                .password(encodedPassword)
                .isTemp(false)
                .user(user)
                .build();
    }
}
