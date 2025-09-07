package com.project.team5backend.domain.space.space.exception;


import com.project.team5backend.global.apiPayload.exception.CustomException;
import lombok.Getter;

@Getter
public class SpaceException extends CustomException {
    public SpaceException(SpaceErrorCode errorCode) {
        super(errorCode);
    }
}