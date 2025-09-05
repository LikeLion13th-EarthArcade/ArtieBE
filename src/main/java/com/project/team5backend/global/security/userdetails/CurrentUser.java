package com.project.team5backend.global.security.userdetails;

import com.project.team5backend.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 비밀번호를 제외한 객체로 비밀번호 외부 유출 최소화
public class CurrentUser {

    private final Long id;

    private final String email;

    private final Role role;
}
