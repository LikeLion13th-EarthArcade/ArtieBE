package com.project.team5backend.domain.auth.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class AuthException extends CustomException {
  public AuthException(AuthErrorCode errorCode) {
    super(errorCode);
  }
}
