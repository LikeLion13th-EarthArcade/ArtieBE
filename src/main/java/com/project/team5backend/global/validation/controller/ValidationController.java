package com.project.team5backend.global.validation.controller;

import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;
import com.project.team5backend.global.validation.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_SIGNUP;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/validations")
@Tag(name = "Validation", description = "인증 관련 API")
public class ValidationController {

    private final ValidationService validationService;

    @Operation(summary = "이메일 인증 코드 발송", description = "회원 가입시에만 사용 <br> 메시지 전송 구현했으나, 일단 resBody로도 제공")
    @PostMapping("/sign-up")
    public CustomResponse<String> sendSignUpCode(
            @RequestBody @Valid ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO
    ) {
        String code = validationService.sendCode(MailType.SIGNUP_VERIFICATION, SCOPE_SIGNUP, emailCodeReqDTO);
        return CustomResponse.onSuccess("메일 발송 성공! code: " + code);
    }
}
