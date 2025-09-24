package com.project.team5backend.domain.recommendation.controller;

import com.project.team5backend.domain.recommendation.dto.response.RecommendResDTO;
import com.project.team5backend.domain.recommendation.service.RecommendationService;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations/personalized")
@Tag(name = "Recommendation",description = "ai 추천 관련 API")
public class RecommendationController {

    private final RecommendationService recommendationService;

    @Operation(summary = "ai 취향 기반 분석", description = "홈페이지에 띄우는 취향 기반 분석 api")
    @GetMapping("/summary")
    public CustomResponse<RecommendResDTO.PersonalizedSummaryResDTO> summary(
            @AuthenticationPrincipal CurrentUser currentUser
            ) {
        return CustomResponse.onSuccess(recommendationService.summary(currentUser.getId()));
    }
    @Operation(summary = "ai 취향 기반 분석 자세히보기", description = "취향 기반 분석을 자세히 보기 했을 때 4개의 결과 반환")
    @GetMapping("/detail")
    public CustomResponse<RecommendResDTO.PersonalizedDetailResDTO> detail(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return CustomResponse.onSuccess(recommendationService.detail(currentUser.getId()));
    }

}