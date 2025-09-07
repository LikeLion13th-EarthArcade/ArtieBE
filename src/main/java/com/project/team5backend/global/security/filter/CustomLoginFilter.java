package com.project.team5backend.global.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.security.dto.JwtDTO;
import com.project.team5backend.global.security.exception.SecurityErrorCode;
import com.project.team5backend.global.security.repository.CustomCookieCsrfTokenRepository;
import com.project.team5backend.global.security.userdetails.CustomUserDetails;
import com.project.team5backend.global.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

import static com.project.team5backend.global.constant.common.CommonConstant.ACCESS_COOKIE_NAME;
import static com.project.team5backend.global.constant.common.CommonConstant.REFRESH_COOKIE_NAME;
import static com.project.team5backend.global.util.CookieUtils.createJwtCookies;

@Slf4j
@RequiredArgsConstructor
public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomCookieCsrfTokenRepository customCookieCsrfTokenRepository;

    //로그인 시도 메서드
    @Override
    public Authentication attemptAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response) throws AuthenticationException {

        log.info("[ Login Filter ]  로그인 시도 : Custom Login Filter 작동 ");
        ObjectMapper objectMapper = new ObjectMapper();
        AuthReqDTO.AuthLoginReqDTO requestBody;
        try {
            requestBody = objectMapper.readValue(request.getInputStream(), AuthReqDTO.AuthLoginReqDTO.class);
        } catch (IOException e) {
            throw new CustomException(SecurityErrorCode.NOT_FOUND);
        }

        //Request Body 에서 추출
        String email = requestBody.email(); //Email 추출
        String password = requestBody.password(); //password 추출
//        log.info("[ Login Filter ]  Email ---> {} ", email);
//        log.info("[ Login Filter ]  Password ---> {} ", password);

        //UserNamePasswordToken 생성 (인증용 객체)
        UsernamePasswordAuthenticationToken authToken
                = new UsernamePasswordAuthenticationToken(email, password, null);


        log.info("[ Login Filter ] 인증용 객체 UsernamePasswordAuthenticationToken 생성");
        log.info("[ Login Filter ] 인증 시도");

        //인증 시도
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시
    @Override
    protected void successfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain,
            @NonNull Authentication authentication) throws IOException {


        log.info("[ Login Filter ] 로그인 성공");

        CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();


        String accessToken = jwtUtil.createJwtAccessToken(customUserDetails); //access token 생성
        String refreshToken = jwtUtil.createJwtRefreshToken(customUserDetails); //refresh token 생성

        long accessExp = jwtUtil.getAccessExpMs();
        long refreshExp = jwtUtil.getRefreshExpMs();

        createJwtCookies(response, ACCESS_COOKIE_NAME, accessToken, accessExp);
        createJwtCookies(response, REFRESH_COOKIE_NAME, refreshToken, refreshExp);

        // 로그인 시 새로운 csrf 토큰 발급
        CsrfToken csrfToken = customCookieCsrfTokenRepository.generateToken(request);
        customCookieCsrfTokenRepository.saveToken(csrfToken, request, response);


        //Client 에게 줄 Response 를 Build
        JwtDTO jwtDTO = JwtDTO.builder()
                .message("쿠키에 저장되었습니다.")
                .accessToken(accessToken) //access token 생성
                .refreshToken(refreshToken) //refresh token 생성
                .build();

        // CustomResponse 사용하여 응답 통일
        CustomResponse<JwtDTO> responseBody = CustomResponse.onSuccess(jwtDTO);

        //JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value()); //Response 의 Status 를 200으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        //Body 에 토큰이 담긴 Response 쓰기
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
    @Override
    protected void unsuccessfulAuthentication(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull AuthenticationException failed) throws IOException {

        log.info("[ Login Filter ] 로그인 실패");

        String errorCode;
        String errorMessage;

        if (failed instanceof BadCredentialsException) {
            errorCode = String.valueOf(HttpStatus.UNAUTHORIZED.value());
            errorMessage = "계정을 찾을 수 없습니다. 아이디 또는 비밀번호를 확인해주세요";
        } else if (failed instanceof LockedException) {
            errorCode = String.valueOf(HttpStatus.LOCKED.value());
            errorMessage = "계정이 잠금 상태입니다.";
        } else if (failed instanceof DisabledException) {
            errorCode = String.valueOf(HttpStatus.FORBIDDEN.value());
            errorMessage = "계정이 비활성화 되었습니다.";
        } else if (failed instanceof UsernameNotFoundException) {
            errorCode = String.valueOf(HttpStatus.NOT_FOUND.value());
            errorMessage = "계정을 찾을 수 없습니다.";
        } else if (failed instanceof AuthenticationServiceException) {
            errorCode = String.valueOf(HttpStatus.BAD_REQUEST.value());
            errorMessage = "Request Body 파싱 중 오류가 발생했습니다.";
        } else {
            errorCode = String.valueOf(HttpStatus.UNAUTHORIZED.value());
            errorMessage = "인증에 실패했습니다.";
        }

        // CustomResponse 사용하여 응답 통일
        CustomResponse<JwtDTO> responseBody = CustomResponse.onFailure(errorCode, errorMessage);

        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(Integer.parseInt(errorCode)); // HTTP 상태 코드 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseBody)); // 응답 변환 및 출력
    }
}
