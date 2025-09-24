package com.project.team5backend.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.global.apiPayload.CustomResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        String code;
        String message;
//        String result = null;
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(403);

        if (accessDeniedException instanceof MissingCsrfTokenException) {
            code = SecurityErrorCode.MISSING_CSRF_TOKEN.getCode();
            message = SecurityErrorCode.MISSING_CSRF_TOKEN.getMessage();
//            result = accessDeniedException.getMessage();
        } else if (accessDeniedException instanceof InvalidCsrfTokenException) {
            code = SecurityErrorCode.INVALID_CSRF_TOKEN.getCode();
            message = SecurityErrorCode.INVALID_CSRF_TOKEN.getMessage();
//            result = accessDeniedException.getMessage();
        } else {
            code = accessDeniedException.getClass().getName();
            message = accessDeniedException.getMessage();
        }
        CustomResponse<Object> forbiddenErrorResponse = CustomResponse.onFailure(code, message, null);
        new ObjectMapper().writeValue(response.getWriter(), forbiddenErrorResponse);
    }
}
