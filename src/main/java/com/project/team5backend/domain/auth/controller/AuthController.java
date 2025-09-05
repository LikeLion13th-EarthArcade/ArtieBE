package com.project.team5backend.domain.auth.controller;


import com.project.team5backend.domain.auth.dto.request.AuthReqDTO;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public CustomResponse<?> login(
            @RequestBody AuthReqDTO.AuthLoginReqDTO authLoginDTO
    ) {
        return null;
    }
}
