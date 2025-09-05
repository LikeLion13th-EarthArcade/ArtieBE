package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.request.UserReqDTO;

import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 API")
public class UserController {

    private final UserCommandService userCommandService;

    @Operation(summary = "회원 가입")
    @PostMapping()
    public CustomResponse<String> createUser(
            @RequestBody UserReqDTO.UserCreateReqDTO userCreateReqDTO
    ) {
        userCommandService.createUser(userCreateReqDTO);
        return CustomResponse.onSuccess(HttpStatus.CREATED, "회원 가입 완료");
    }

}
