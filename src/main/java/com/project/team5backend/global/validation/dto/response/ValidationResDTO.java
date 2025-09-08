package com.project.team5backend.global.validation.dto.response;

import lombok.Builder;
import org.w3c.dom.CDATASection;

import java.util.List;

public class ValidationResDTO {

    @Builder
    public record BizNumberValidationResDTO(
            boolean isValid,
            boolean isExpired,
            Info info
    ) {
        @Builder
        public record Info(
                String bizNumber,
                String bizStatus,
                String bizStatusCode,
                String taxType,
                String taxTypeCode,
                String endAt
        ) {
        }
    }
}
