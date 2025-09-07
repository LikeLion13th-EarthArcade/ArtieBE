package com.project.team5backend.global.security.filter;

import com.project.team5backend.domain.user.entity.Role;
import com.project.team5backend.global.security.userdetails.CustomUserDetails;
import com.project.team5backend.global.security.util.JwtUtil;
import com.project.team5backend.global.util.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static com.project.team5backend.global.constant.common.CommonConstant.ACCESS_COOKIE_NAME;
import static com.project.team5backend.global.constant.redis.RedisConstant.KEY_BLACK_LIST_SUFFIX;
import static com.project.team5backend.global.util.CookieUtils.getTokenFromCookies;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // JWT 관련 유틸리티 클래스 주입
    private final JwtUtil jwtUtil;

    // redis 주입
    private final RedisUtils<String> redisUtils;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("[ JwtAuthorizationFilter ] 인가 필터 작동");

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.info("[ JwtAuthorizationFilter ] 검색된 쿠키 -----------------------");
            for (Cookie cookie : cookies) {
                log.info("[ JwtAuthorizationFilter ] 쿠키명 : {}, 값 : {}...", cookie.getName(), cookie.getValue().substring(0, 15));
            }
            log.info("[ JwtAuthorizationFilter ] ---------------------------------");
        } else {
            log.warn("[ JwtAuthorizationFilter ] 현재 쿠키 없음");
        }

        try {
            // 1. Cookie 에서 Access Token 추출
            String accessToken = getTokenFromCookies(request, ACCESS_COOKIE_NAME);

            // 2. Access Token이 없으면 다음 필터로 바로 진행
            if (accessToken == null) {
                log.info("[ JwtAuthorizationFilter ] Access Token 없음, 다음 필터 진행");
                filterChain.doFilter(request, response);
                return;
            }

            log.info("[ JwtAuthorizationFilter ] 로그아웃 여부 확인");
            if (Objects.equals(accessToken, redisUtils.get(jwtUtil.getJti(accessToken) + KEY_BLACK_LIST_SUFFIX))) {
                log.info("[ JwtAuthorizationFilter ] 블랙리스트 토큰, 인증 생략 후 다음 필터 진행");
                filterChain.doFilter(request, response);
                return;
            }

            // 3. Access Token을 이용한 인증 처리
            authenticateAccessToken(accessToken);
            log.info("[ JwtAuthorizationFilter ] 다음 필터 진행");

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Access Token 이 만료되었습니다.");
        }
    }

    // Access Token을 바탕으로 인증 객체 생성 및 SecurityContext에 저장
    private void authenticateAccessToken(String accessToken) {
        log.info("[ JwtAuthorizationFilter ] 토큰으로 인가 과정을 시작");

        // 1. Access Token의 유효성 검증
        jwtUtil.validateToken(accessToken);
        log.info("[ JwtAuthorizationFilter ] Access Token 유효성 검증 성공");

        // 2. Access Token에서 사용자 정보 추출 후 CustomUserDetails 생성
        Long userId = jwtUtil.getId(accessToken);
        String email = jwtUtil.getEmail(accessToken);
        Role role = jwtUtil.getRoles(accessToken);
        log.info("[ JwtAuthorizationFilter ] userId = {}, email = {}, role = {}", userId, email, role);

        CustomUserDetails userDetails = new CustomUserDetails(userId, email, null, role);

        log.info("[ JwtAuthorizationFilter ] UserDetails 객체 생성 성공");

        // 3. 인증 객체 생성 및 SecurityContextHolder에 저장
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        log.info("[ JwtAuthorizationFilter ] 인증 객체 생성 완료");

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        log.info("[ JwtAuthorizationFilter ] 인증 객체 저장 완료");
    }

}
