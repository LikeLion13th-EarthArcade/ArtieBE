package com.project.team5backend.global.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MailType {
    SIGNUP_VERIFICATION("[Artie] - 이메일 인증 코드", "mail/template/code.html"),
    TEMP_PASSWORD_VERIFICATION("[Artie] - 임시 비밀번호 발급 이메일 인증 코드", "mail/template/code.html"),
    TEMP_PASSWORD_SEND("[Artie] - 임시 비밀번호 발급", "mail/template/temp-password.html"),
    ;
    private final String subject;
    private final String templatePath;
}
