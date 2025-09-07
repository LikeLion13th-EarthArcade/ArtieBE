package com.project.team5backend.global.validation.service;

import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;

public interface ValidationService {

    String sendCode(MailType mailType, String scope, ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO);

    void verifyCode(ValidationReqDTO.EmailCodeValidationReqDTO emailCodeValidationReqDTO);
}
