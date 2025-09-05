package com.project.team5backend.global.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team5backend.global.apiPayload.CustomResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CsrfAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(403);

        if  (accessDeniedException instanceof MissingCsrfTokenException) {
            CustomResponse<Object> errorResponse = CustomResponse.onFailure(
                    SecurityErrorCode.MISSING_CSRF_TOKEN.getCode(),
                    SecurityErrorCode.MISSING_CSRF_TOKEN.getMessage(),
                    null
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);

        } else if (accessDeniedException instanceof InvalidCsrfTokenException) {
            CustomResponse<Object> errorResponse = CustomResponse.onFailure(
                    SecurityErrorCode.INVALID_CSRF_TOKEN.getCode(),
                    SecurityErrorCode.INVALID_CSRF_TOKEN.getMessage(),
                    null
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorResponse);
        }

    }

}
