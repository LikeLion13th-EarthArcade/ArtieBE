package com.project.team5backend.global.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.security.exception.SecurityErrorCode;
import com.project.team5backend.global.security.util.JwtUtil;
import com.project.team5backend.global.util.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.project.team5backend.global.constant.redis.RedisConstant.KEY_REFRESH_SUFFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final JwtUtil jwtUtil;
    private final RedisUtils<String> redisUtils;

    public String reissueCookie(String refreshToken) {
        // 4. 토큰 만료 시 refreshToken으로 AccessToken 재발급

        if (refreshToken == null) {
            // 리프레시 토큰 조차 만료 -> 재 로그인 안내
            throw new CustomException(SecurityErrorCode.REQUIRED_RE_LOGIN);
        }

        // refresh token의 유효성 검사
        log.info("[ reissueCookie ] refresh token의 유효성을 검사합니다.");
        try {
            jwtUtil.validateToken(refreshToken);
            // redis 에 해당 refresh token이 존재하는지 검사
            if (!Objects.equals(redisUtils.get(jwtUtil.getEmail(refreshToken) + KEY_REFRESH_SUFFIX), refreshToken)) {
                // 서버에 리프레시 토큰이 없음 -> 재 로그인 안내
                throw new CustomException(SecurityErrorCode.REQUIRED_RE_LOGIN);
            }
        } catch (ExpiredJwtException e) {
            throw new CustomException(SecurityErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // access token 재발급
        log.info("[ reissueCookie ] refresh token 으로 access token 을 생성합니다.");
        return jwtUtil.reissueToken(refreshToken);
    }
}
