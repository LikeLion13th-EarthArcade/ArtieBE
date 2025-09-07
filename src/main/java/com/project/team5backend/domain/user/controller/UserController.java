package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.request.UserReqDTO;

import com.project.team5backend.domain.user.dto.response.UserResDTO;
import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.domain.user.service.query.UserQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 API")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @Operation(summary = "회원 가입")
    @PostMapping()
    public CustomResponse<String> createUser(
            @RequestBody UserReqDTO.UserCreateReqDTO userCreateReqDTO
    ) {
        userCommandService.createUser(userCreateReqDTO);
        return CustomResponse.onSuccess(HttpStatus.CREATED, "회원 가입 완료");
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("me")
    public CustomResponse<UserResDTO.UserProfileResDTO> getUserProfile(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return CustomResponse.onSuccess(userQueryService.getUserProfile(currentUser.getId()));
    }

    @Operation(summary = "회원 정보 수정")
    @PatchMapping("me")
    public CustomResponse<String> updateUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody UserReqDTO.UserUpdateReqDTO userUpdateReqDTO
    ) {
        userCommandService.updateUser(currentUser.getId(), userUpdateReqDTO);
        return CustomResponse.onSuccess("회원 정보 수정 완료");
    }
}
