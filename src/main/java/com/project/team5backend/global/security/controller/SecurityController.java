package com.project.team5backend.global.security.controller;

import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.service.SecurityService;
import com.project.team5backend.global.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.team5backend.global.constant.common.CommonConstant.ACCESS_COOKIE_NAME;
import static com.project.team5backend.global.constant.common.CommonConstant.REFRESH_COOKIE_NAME;
import static com.project.team5backend.global.util.CookieUtils.createJwtCookies;
import static com.project.team5backend.global.util.CookieUtils.getTokenFromCookies;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/security")
@Tag(name = "Security", description = "시큐리티 관련 API")
public class SecurityController {

    private final SecurityService securityService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "엑세스 쿠키 재발급", description = "엑세스 쿠키가 만료되어 없어졌고, 리프레시 쿠키가 있다면 엑세스 쿠키를 만들어준다.")
    @PostMapping("/reissue-cookie")
    public CustomResponse<String> reissueCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = getTokenFromCookies(request, REFRESH_COOKIE_NAME);
        String accessToken = securityService.reissueCookie(refreshToken);

        // 쿠키 재발급
        log.info("[ JwtAuthorizationFilter ] 쿠키를 재생성 합니다.");
        createJwtCookies(response, ACCESS_COOKIE_NAME, accessToken, jwtUtil.getAccessExpMs());

        return CustomResponse.onSuccess("엑세스 쿠키가 재발급 되었습니다.");
    }

    @Operation(summary = "CSRF 토큰 발급", description = "CSRF 토큰을 쿠키로 발급합니다")
    @GetMapping("/csrf")
    public CustomResponse<String> csrf(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String host = request.getHeader("Host");
        String remoteAddr = request.getRemoteAddr();

        log.info("[CSRF] 요청 도메인 Origin   : {}", origin);
        log.info("[CSRF] 요청 Referer (페이지): {}", referer);
        log.info("[CSRF] 요청 Host (서버)     : {}", host);
        log.info("[CSRF] 요청 Client IP       : {}", remoteAddr);

        // lazy 토큰을 실제로 생성해서 쿠키에 담기도록 트리거
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token != null) {
            token.getToken(); // 호출 시 쿠키가 Set-Cookie 로 내려감
        }

        return CustomResponse.onSuccess("CSRF 토큰이 쿠키로 발급되었습니다.    " + token.getToken());
    }
}
