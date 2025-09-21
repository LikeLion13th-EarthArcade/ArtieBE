package com.project.team5backend.domain.review.space.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class SpaceReviewException extends CustomException {
  public SpaceReviewException(SpaceReviewErrorCode errorCode) {
    super(errorCode);
  }
}
