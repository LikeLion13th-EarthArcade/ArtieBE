package com.project.team5backend.domain.auth.controller;


import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "AUTH", description = "AUTH 관련 API")
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public CustomResponse<?> login(
            @RequestBody AuthReqDTO.AuthLoginReqDTO authLoginDTO
    ) {
        return null;
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public CustomResponse<?> logout() {
        return null;
    }

    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/me/password")
    public CustomResponse<String> changePassword(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid AuthReqDTO.AuthPasswordChangeReqDTO authPasswordChangeReqDTO
    ) {
        authCommandService.changePassword(currentUser.getId(), authPasswordChangeReqDTO);
        return CustomResponse.onSuccess("비밀번호 변경 완료");
    }

    @Operation(summary = "임시 비밀번호 발급", description = "비밀번호 변경 휴대폰 인증 완료 후 실행 <br> 이메일 전송 구현 완료했지만, 비밀번호는 일단 ResBody로 제공")
    @PostMapping("/temp-password")
    public CustomResponse<String> resetPassword(
            @RequestBody @Valid AuthReqDTO.AuthTempPasswordReqDTO authTempPasswordReqDto
    ) {
        String password = authCommandService.tempPassword(authTempPasswordReqDto);
        return CustomResponse.onSuccess("임시 비빌번호가 이메일로 발송되었습니다. " + password);
    }
}
