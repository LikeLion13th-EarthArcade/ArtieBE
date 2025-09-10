package com.project.team5backend.global.apiPayload.exception.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import com.project.team5backend.global.apiPayload.code.GeneralErrorCode;
import com.project.team5backend.global.apiPayload.exception.CustomException;
import com.project.team5backend.global.security.exception.SecurityErrorCode;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //애플리케이션에서 발생하는 커스텀 예외를 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<Void>> handleCustomException(CustomException ex) {
        BaseErrorCode errorCode = ex.getCode();
        //예외가 발생하면 로그 기록
        log.warn("[ CustomException ]: {}", ex.getCode().getMessage());
        //커스텀 예외에 정의된 에러 코드와 메시지를 포함한 응답 제공
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorCode.getErrorResponse());
    }

    // MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("[ Validation Error - MethodArgumentNotValidException ]: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        BaseErrorCode errorCode = GeneralErrorCode.VALIDATION_FAILED_DTO_FILED;
        CustomResponse<Map<String, String>> errorResponse = CustomResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), errors);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    // ConstraintViolationException
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("[ Validation Error - ConstraintViolationException ]: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });

        BaseErrorCode errorCode = GeneralErrorCode.VALIDATION_FAILED_PARAM;
        CustomResponse<Map<String, String>> errorResponse = CustomResponse.onFailure(errorCode.getCode(), errorCode.getMessage(), errors);
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    // JSON 오류
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("[ HttpMessageNotReadableException ]: {}", ex.getMessage());

        BaseErrorCode errorCode = getBaseErrorCode(ex);
        CustomResponse<String> errorResponse = CustomResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponse<String>> handleAuthorizationDenied(AccessDeniedException ex) {
        log.warn("[ AccessDeniedException ]: {}", ex.getMessage());
        BaseErrorCode errorCode;
        String code;
        String message;

        if (ex instanceof AuthorizationDeniedException) {
            code = SecurityErrorCode.ROLE_ACCESS_DENIED.getCode();
            message = SecurityErrorCode.ROLE_ACCESS_DENIED.getMessage();
        } else {
            code = "AccessDeniedException";
            message = ex.getMessage();
        }
        CustomResponse<String> errorResponse = CustomResponse.onFailure(
                code,
                message,
                null
        );
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    // 그 외의 정의되지 않은 모든 예외 처리
    @ExceptionHandler({Exception.class})
    public ResponseEntity<CustomResponse<String>> handleAllException(Exception ex) {
        log.error("[WARNING] Internal Server Error : {} ", ex.getMessage(), ex);
        BaseErrorCode errorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR_500;
        CustomResponse<String> errorResponse = CustomResponse.onFailure(
                errorCode.getCode(),
                errorCode.getMessage(),
                null
        );
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(errorResponse);
    }

    private static BaseErrorCode getBaseErrorCode(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        BaseErrorCode errorCode;
        // 1) JSON 문법 자체가 잘못됨 (콤마/따옴표 등)
        if (cause instanceof JsonParseException) {
            errorCode = GeneralErrorCode.INVALID_JSON_SYNTAX;
        }
        // 2) 타입/형식이 맞지 않음 (ex: string → int)
        else if (cause instanceof InvalidFormatException e) {
            Class<?> targetType = e.getTargetType();
            if (targetType.equals(LocalDate.class)) {
                errorCode = GeneralErrorCode.INVALID_LOCAL_DATE;
            }
            else if (targetType.isEnum()) {
                errorCode = GeneralErrorCode.INVALID_ENUM;
            }
            else {
                errorCode = GeneralErrorCode.INVALID_FIELD_FORMAT;
            }
        }
        // 3) 필수 필드 누락 등 바인딩 실패
        else if (cause instanceof MismatchedInputException) {
            errorCode = GeneralErrorCode.INVALID_INPUT;
        }
        // 그 외 일반 케이스
        else {
            errorCode = GeneralErrorCode.BAD_REQUEST_BODY;
        }
        return errorCode;
    }
}