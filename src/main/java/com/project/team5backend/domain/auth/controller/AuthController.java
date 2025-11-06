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

    @Operation(summary = "로그인", description = "새로운 csrf 토큰이 발급됩니다")
    @PostMapping("/login")
    public CustomResponse<?> login(
            @RequestBody AuthReqDTO.AuthLoginReqDTO authLoginDTO
    ) {
        return null;
    }

    @Operation(summary = "로그아웃", description = "모든 쿠키가 삭제되고, 새로운 csrf 토큰이 발급됩니다")
    @PostMapping("/logout")
    public CustomResponse<?> logout() {
        return null;
    }

    @Operation(summary = "비밀번호 변경",
            description = "현재 비밀번호와 다르거나, 비밀번호 확인이 일치하지 않거나, 바꾸려는 비밀번호가 현재 비밀번호와 일치할 경우 예외")
    @PatchMapping("/me/password")
    public CustomResponse<String> changePassword(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid AuthReqDTO.AuthPasswordChangeReqDTO authPasswordChangeReqDTO
    ) {
        authCommandService.changePassword(currentUser.getId(), authPasswordChangeReqDTO);
        return CustomResponse.onSuccess("비밀번호 변경 완료");
    }

    @Operation(summary = "비밀번호 재설정(잃어버렸을 때)", description = "비밀번호 재설정 이메일 인증 완료 후 실행 <br> 비밀번호 확인이 일치하지 않거나, 바꾸려는 비밀번호가 현재 비밀번호와 일치할 경우 예외")
    @PostMapping("/reset-password")
    public CustomResponse<String> resetPassword(
            @RequestBody @Valid AuthReqDTO.AuthResetPasswordReqDTO authResetPasswordReqDTO
    ) {
        authCommandService.resetPassword(authResetPasswordReqDTO);
        return CustomResponse.onSuccess("비밀번호 재설정 완료");
    }
}
