package com.project.team5backend.domain.auth.controller;


import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.domain.auth.service.command.AuthCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public CustomResponse<?> login(
            @RequestBody AuthReqDTO.AuthLoginReqDTO authLoginDTO
    ) {
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
}
