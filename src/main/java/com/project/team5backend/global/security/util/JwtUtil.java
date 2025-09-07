package com.project.team5backend.global.security.util;

import com.project.team5backend.domain.user.entity.Role;
import com.project.team5backend.global.security.userdetails.CustomUserDetails;
import com.project.team5backend.global.util.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.project.team5backend.global.constant.redis.RedisConstant.*;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final Long accessExpMs;
    private final Long refreshExpMs;
    private final RedisUtils<String> redisUtils;

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.token.access-expiration-time}") Long access,
            @Value("${spring.jwt.token.refresh-expiration-time}") Long refresh,
            RedisUtils<String> redisUtils
    ) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),Jwts.SIG.HS256.key().build().getAlgorithm());
        accessExpMs = access;
        refreshExpMs = refresh;
        this.redisUtils = redisUtils;
    }

    // JWT 토큰을 입력으로 받아 토큰의 subject 로부터 사용자 Email 추출하는 메서드
    public String getEmail(String token) throws SignatureException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // JWT 토큰을 입력으로 받아 토큰의 claim 에서 사용자 권한을 추출하는 메서드
    public Role getRoles(String token) throws SignatureException{
        String roleStr = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public Long getId(String token) throws SignatureException{
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("userId", Long.class);
    }

    public String getJti(String token) throws SignatureException{
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("jti", String.class);
    }

    public void saveBlackListToken(String email, String accessToken, String refreshToken) {
        // 토큰의 jti를 가져옴
        final String accessJti = getJti(accessToken);
        final String refreshJti = getJti(refreshToken);
        // 토큰 블랙리스트 등록
        redisUtils.save(accessJti + KEY_BLACK_LIST_SUFFIX, accessToken, getRefreshExpMs(), TimeUnit.MILLISECONDS);
        redisUtils.save(refreshJti + KEY_BLACK_LIST_SUFFIX, refreshToken, getAccessExpMs(), TimeUnit.MILLISECONDS);
        // 리프레시 정보 삭제
        redisUtils.delete(email + KEY_REFRESH_SUFFIX);

    }

    public long getAccessExpMs() {
        return this.accessExpMs;
    }

    public long getRefreshExpMs() {
        return this.refreshExpMs;
    }

    // Token 발급하는 메서드
    public String tokenProvider(CustomUserDetails customUserDetails, Instant expiration) {
        //현재 시간
        Instant issuedAt = Instant.now();
        // 토큰에 부여할 고유 jti
        final String jti = UUID.randomUUID().toString();

        //토큰에 부여할 권한
        String authorities = customUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header() //헤더 부분
                .add("typ", "JWT") // JWT type
                .and()
                .id(jti)
                .subject(customUserDetails.getUsername()) //Subject 에 username (email) 추가
                .claim("userId", customUserDetails.getId())
                .claim("role", authorities) //권한 추가
                .issuedAt(Date.from(issuedAt)) // 현재 시간 추가
                .expiration(Date.from(expiration)) //만료 시간 추가
                .signWith(secretKey) //signature 추가
                .compact(); //합치기
    }

    // principalDetails 객체에 대해 새로운 JWT 액세스 토큰을 생성
    public String createJwtAccessToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(accessExpMs);
        return tokenProvider(customUserDetails, expiration);
    }

    // principalDetails 객체에 대해 새로운 JWT 리프레시 토큰을 생성
    public String createJwtRefreshToken(CustomUserDetails customUserDetails) {
        Instant expiration = Instant.now().plusMillis(refreshExpMs);
        String refreshToken = tokenProvider(customUserDetails, expiration);

        // Redis 에 Refresh Token 저장
        redisUtils.save(
                // {email}:refresh -> refreshToken
                customUserDetails.getUsername() + KEY_REFRESH_SUFFIX,
                refreshToken,
                refreshExpMs,
                TimeUnit.MILLISECONDS
        );
        return refreshToken;
    }

    // 제공된 리프레시 토큰을 기반으로 access token을 다시 발급
    public String reissueToken(String refreshToken) throws SignatureException {
        // refreshToken 에서 user 정보를 가져와서 새로운 토큰을 발급 (발급 시간, 유효 시간(reset)만 새로 적용)
        // 재발급시에는 비밀번호를 넣지 않아 비밀번호 노출 억제
        CustomUserDetails userDetails = new CustomUserDetails(
                getId(refreshToken),
                getEmail(refreshToken),
                null,
                getRoles(refreshToken)
        );
        log.info("[ JwtUtil ] 새로운 토큰을 재발급");

        // 재발급
        return createJwtAccessToken(userDetails);
    }

    public void validateToken(String token) {
        log.info("[ JwtUtil ] 토큰의 유효성 검증");
        try {
            // 구문 분석 시스템의 시계가 JWT를 생성한 시스템의 시계 오차 고려
            // 약 3분 허용.
            long seconds = 3 *60;
            boolean isExpired = Jwts
                    .parser()
                    .clockSkewSeconds(seconds)
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
            if (isExpired) {
                log.info("만료된 JWT 토큰");
            }

        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            //원하는 Exception throw
            throw new SecurityException("잘못된 토큰");
        } catch (ExpiredJwtException e) {
            //원하는 Exception throw
            throw new ExpiredJwtException(null, null, "만료된 JWT 토큰");
        }
    }
}
