package com.project.team5backend.global.validation.controller;

import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.mail.MailType;
import com.project.team5backend.global.validation.dto.request.ValidationReqDTO;
import com.project.team5backend.global.validation.dto.response.ValidationResDTO;
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
import static com.project.team5backend.global.constant.scope.ScopeConstant.SCOPE_RESET_PASSWORD;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/validations")
@Tag(name = "Validation", description = "인증 관련 API")
public class ValidationController {

    private final ValidationService validationService;

    @Operation(summary = "회원 가입 이메일 인증 코드 발송",
            description = "회원 가입시에만 사용<br> " +
                    "실제 이메일 발송됩니다, 일단 resBody로도 제공 / 이메일 모양새는 gpt가 만들어줌 <br>" +
                    "이메일 인증은 서버 안정성을 위해 10초의 대기시간 존재<br>" +
                    "이메일 인증 코드는 5분간 유효, 인증 성공시 해당 이메일은 15분간 타인의 회원가입에 사용불가<br>" +
                    "인증 완료 후 15분까지 회원 가입을 하지 못하면 해당 이메일로 재인증 필요, 가입 제한<br>" +
                    "이메일 형식을 지키지 않으면 예외")
    @PostMapping("/code/sign-up")
    public CustomResponse<String> sendSignUpCode(
            @RequestBody @Valid ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO
    ) {
        String code = validationService.sendCode(MailType.SIGNUP_VERIFICATION, SCOPE_SIGNUP, emailCodeReqDTO);
        return CustomResponse.onSuccess("메일 발송 성공! code: " + code);
    }

    @Operation(summary = "비밀번호 재설정 이메일 인증 코드 발송")
    @PostMapping("/code/reset-password")
    public CustomResponse<String> sendResetPasswordCode(
            @RequestBody @Valid ValidationReqDTO.EmailCodeReqDTO emailCodeReqDTO
    ) {
        String code = validationService.sendCode(MailType.TEMP_PASSWORD_VERIFICATION, SCOPE_RESET_PASSWORD, emailCodeReqDTO);
        return CustomResponse.onSuccess("메일 발송 성공! code: " + code);
    }

    @Operation(summary = "이메일 인증 코드 검증",
            description = "각 코드 요청에 따른 인증 정보를 서버에 15분간 저장<br>" +
                    "15분이 지나면 재인증 필요 -> 코드 인증을 필요로 하는 모든 서비스 이용 불가<br>" +
                    "인증 코드는 정수 6자리이며, 적절한 이메일 형식 -> 아닐시 예외")
    @PostMapping("/code/confirmation")
    public CustomResponse<String> verifyCode(
            @RequestBody @Valid ValidationReqDTO.EmailCodeValidationReqDTO emailCodeValidationReqDTO
    ) {
        validationService.verifyCode(emailCodeValidationReqDTO);
        return CustomResponse.onSuccess("이메일 인증 성공!");
    }

    @Operation(summary = "사업자 번호 검증",
            description = "사업자 등록이 유효한 경우 isValid = true, 만료된 경우 isExpired = true")
    @PostMapping("/biz-number/confirmation")
    public CustomResponse<ValidationResDTO.BizNumberValidationResDTO> verifyBizNumber(
            @RequestBody @Valid ValidationReqDTO.BizNumberValidationReqDTO bizNumberValidationReqDTO
    ) {
        return CustomResponse.onSuccess(validationService.verifyBizNumber(bizNumberValidationReqDTO));
    }
}
