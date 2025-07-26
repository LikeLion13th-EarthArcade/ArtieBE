package com.project.team5backend.global.apiPayload.exception;

import com.project.team5backend.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

  private final BaseErrorCode code;

  public CustomException(BaseErrorCode errorCode) {
    this.code = errorCode;
  }
}