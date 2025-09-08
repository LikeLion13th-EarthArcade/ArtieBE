package com.project.team5backend.global.validation.service;

import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;
import com.project.team5backend.global.validation.dto.response.ValidationResDTO;

public interface ValidationService {

    String sendCode(MailType mailType, String scope, ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO);

    void verifyCode(ValidationReqDTO.EmailCodeValidationReqDTO emailCodeValidationReqDTO);

    ValidationResDTO.BizNumberValidationResDTO verifyBizNumber(ValidationReqDTO.BizNumberValidationReqDTO bizNumberValidationReqDTO);
}
