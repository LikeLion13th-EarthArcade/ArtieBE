package com.project.team5backend.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.global.apiPayload.CustomResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("[ CustomLogoutSuccessHandler  ] 로그아웃 성공");
        // CustomResponse 사용하여 응답 통일
        CustomResponse<String> responseBody = CustomResponse.onSuccess("로그아웃 성공");

        //JSON 변환
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(HttpStatus.OK.value()); //Response 의 Status 를 200으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        //Body 에 토큰이 담긴 Response 쓰기
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}

