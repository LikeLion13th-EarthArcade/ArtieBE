package com.project.team5backend.global.validation.converter;


import com.project.team5backend.global.biznumber.dto.response.BizNumberResDTO;
import com.project.team5backend.global.validation.dto.response.ValidationResDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationConverter {

    public static ValidationResDTO.BizNumberValidationResDTO toBizNumberValidationResDTO(ValidationResDTO.BizNumberValidationResDTO.Info info, boolean isValid, boolean isExpired) {
        return ValidationResDTO.BizNumberValidationResDTO.builder()
                .isValid(isValid)
                .isExpired(isExpired)
                .info(info)
                .build();
    }

    public static ValidationResDTO.BizNumberValidationResDTO.Info toInfo(BizNumberResDTO.BizInfo.InfoItem infoItem) {
        return ValidationResDTO.BizNumberValidationResDTO.Info.builder()
                .bizNumber(infoItem.b_no())
                .bizStatus(infoItem.b_stt())
                .bizStatusCode(infoItem.b_stt_cd())
                .taxType(infoItem.tax_type())
                .taxTypeCode(infoItem.tax_type_cd())
                .endAt(infoItem.end_dt())
                .build();
    }
}
