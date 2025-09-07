package com.project.team5backend.global.security.repository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.util.StringUtils;

import java.util.UUID;

import static com.project.team5backend.global.constant.common.CommonConstant.CSRF_COOKIE_NAME;
import static com.project.team5backend.global.util.CookieUtils.createCsrfCookies;
import static com.project.team5backend.global.util.CookieUtils.deleteCsrfCookie;

public class CustomCookieCsrfTokenRepository implements CsrfTokenRepository {

    public static final String CSRF_HEADER_NAME = "X-XSRF-TOKEN";
    private static final String CSRF_PARAMETER_NAME = "_csrf";

    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        String token = UUID.randomUUID().toString();
        return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, token);
    }

    @Override
    public void saveToken(CsrfToken csrfToken, HttpServletRequest request, HttpServletResponse response) {
        if (response == null) return;

        if (csrfToken == null) {
            // 여기서 바로 삭제하지 않음 (로그아웃 시 직접 전용 메서드 호출로 삭제)
            return;
        }

        createCsrfCookies(response, CSRF_COOKIE_NAME, csrfToken);
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (CSRF_COOKIE_NAME.equals(c.getName()) && StringUtils.hasText(c.getValue())) {
                    return new DefaultCsrfToken(CSRF_HEADER_NAME, CSRF_PARAMETER_NAME, c.getValue());
                }
            }
        }
        return null;
    }

    public void invalidateCsrfToken(HttpServletResponse response) {
        deleteCsrfCookie(response);
    }
}
