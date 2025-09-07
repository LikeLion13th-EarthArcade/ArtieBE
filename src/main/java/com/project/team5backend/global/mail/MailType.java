package com.project.team5backend.global.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailType {
    SIGNUP_VERIFICATION("[Artie] - 이메일 인증 코드", "mail/template/code.html"),
    ;
    private final String subject;
    private final String templatePath;
}
