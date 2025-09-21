package com.project.team5backend.domain.review.exhibition.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class ExhibitionReviewException extends CustomException {
  public ExhibitionReviewException(ExhibitionReviewErrorCode errorCode) {
    super(errorCode);
  }
}
