package com.project.team5backend.global.security.handler;

import com.project.team5backend.global.security.repository.CustomCookieCsrfTokenRepository;
import com.project.team5backend.global.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import static com.project.team5backend.global.constant.common.CommonConstant.ACCESS_COOKIE_NAME;
import static com.project.team5backend.global.constant.common.CommonConstant.REFRESH_COOKIE_NAME;
import static com.project.team5backend.global.util.CookieUtils.createJwtCookies;
import static com.project.team5backend.global.util.CookieUtils.getTokenFromCookies;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final JwtUtil jwtUtil;
    private final CustomCookieCsrfTokenRepository customCookieCsrfTokenRepository;


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String accessToken = getTokenFromCookies(request, ACCESS_COOKIE_NAME);
        String refreshToken = getTokenFromCookies(request, REFRESH_COOKIE_NAME);
        String email = jwtUtil.getEmail(refreshToken);

        jwtUtil.saveBlackListToken(email, accessToken, refreshToken);

        createJwtCookies(response, ACCESS_COOKIE_NAME, null, 0);
        createJwtCookies(response, REFRESH_COOKIE_NAME, null, 0);

        customCookieCsrfTokenRepository.invalidateCsrfToken(response);

        // 로그아웃 시 새로운 csrf 토큰 발급
        CsrfToken csrfToken = customCookieCsrfTokenRepository.generateToken(request);
        customCookieCsrfTokenRepository.saveToken(csrfToken, request, response);

        log.info("[ CustomLogoutHandler ] 쿠키 삭제 완료");
    }
}
