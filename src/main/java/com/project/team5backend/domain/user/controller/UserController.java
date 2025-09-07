package com.project.team5backend.domain.user.controller;


import com.project.team5backend.domain.user.dto.request.UserReqDTO;

import com.project.team5backend.domain.user.dto.response.UserResDTO;
import com.project.team5backend.domain.user.service.command.UserCommandService;
import com.project.team5backend.domain.user.service.query.UserQueryService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.repository.CustomCookieCsrfTokenRepository;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import com.project.team5backend.global.security.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.project.team5backend.global.constant.common.CommonConstant.ACCESS_COOKIE_NAME;
import static com.project.team5backend.global.constant.common.CommonConstant.REFRESH_COOKIE_NAME;
import static com.project.team5backend.global.util.CookieUtils.createJwtCookies;
import static com.project.team5backend.global.util.CookieUtils.getTokenFromCookies;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "USER", description = "유저 관련 API")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final CustomCookieCsrfTokenRepository csrfTokenRepository;

    @Operation(summary = "회원 가입",
            description = "회원 가입 이메일 인증 코드 발송 + 이메일 인증 코드 검증을 순서대로 수행하지 않으면 가입 불가<br>" +
                    "필드 입력이 서버가 기대하는 값과 다른 경우 예외 발생<br>" +
                    "email -> 빈칸일 수 없음, 이메일 형식 준수<br>" +
                    "password -> 공백없는 8-16자리의 대/소문자+숫자+특수문자만 허용, 사용불가 특수문자 : < > { } | ; ' \"<br>" +
                    "passwordConfirmation -> password와 동일 & 일치 여부<br>" +
                    "name -> 이름은 한글만 가능하게 설정 가능하긴 한데, 아직은 그냥 빈칸일 수 없음")
    @PostMapping()
    public CustomResponse<String> createUser(
            @RequestBody @Valid UserReqDTO.UserCreateReqDTO userCreateReqDTO
    ) {
        userCommandService.createUser(userCreateReqDTO);
        return CustomResponse.onSuccess(HttpStatus.CREATED, "회원 가입 완료");
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/me")
    public CustomResponse<UserResDTO.UserProfileResDTO> getUserProfile(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return CustomResponse.onSuccess(userQueryService.getUserProfile(currentUser.getId()));
    }

    @Operation(summary = "회원 정보 수정",
            description = "필드 입력이 서버가 기대하는 값과 다른 경우 예외 발생<br>" +
                    "name -> 이름은 한글만 가능하게 설정 가능하긴 한데, 아직은 그냥 빈칸일 수 없음")
    @PatchMapping("/me")
    public CustomResponse<String> updateUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody @Valid UserReqDTO.UserUpdateReqDTO userUpdateReqDTO
    ) {
        userCommandService.updateUser(currentUser.getId(), userUpdateReqDTO);
        return CustomResponse.onSuccess("회원 정보 수정 완료");
    }

    // TODO : 회원 탈퇴시 회원과 관련된 정보를 놔둘것이냐? 삭제할것이냐? 연쇄적으로 소프트 딜리트할 것이냐?
    @Operation(summary = "회원 탈퇴", description = "인증 관련 쿠키 모두 삭제 + 블랙리스트")
    @DeleteMapping("/withdrawal")
    public CustomResponse<String> withdrawalUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final String accessToken = getTokenFromCookies(request, ACCESS_COOKIE_NAME);
        final String refreshToken = getTokenFromCookies(request, REFRESH_COOKIE_NAME);

        userCommandService.withdrawalUser(currentUser.getId(), accessToken, refreshToken);

        createJwtCookies(response, ACCESS_COOKIE_NAME, null, 0);
        createJwtCookies(response, REFRESH_COOKIE_NAME, null, 0);
        csrfTokenRepository.invalidateCsrfToken(response);

        return CustomResponse.onSuccess(HttpStatus.NO_CONTENT, "회원 탈퇴 완료");
    }
}
