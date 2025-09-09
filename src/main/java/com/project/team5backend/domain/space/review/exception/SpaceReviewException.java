package com.project.team5backend.domain.space.review.exception;

import com.project.team5backend.global.apiPayload.exception.CustomException;

public class SpaceReviewException extends CustomException {
  public SpaceReviewException(SpaceReviewErrorCode errorCode) {
    super(errorCode);
  }
}
