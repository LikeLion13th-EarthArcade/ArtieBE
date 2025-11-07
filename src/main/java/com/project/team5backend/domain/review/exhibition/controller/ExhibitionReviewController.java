package com.project.team5backend.domain.review.exhibition.controller;

import com.project.team5backend.domain.review.exhibition.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.review.exhibition.service.command.ExhibitionReviewCommandService;
import com.project.team5backend.domain.review.exhibition.service.query.ExhibitionReviewQueryService;
import com.project.team5backend.domain.common.enums.Sort;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import com.project.team5backend.global.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "ExhibitionReview", description = "전시 리뷰 관련 API")
public class ExhibitionReviewController {
    private final ExhibitionReviewCommandService exhibitionReviewCommandService;
    private final ExhibitionReviewQueryService exhibitionReviewQueryService;

    @SwaggerBody(content = @Content(
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
                    @Encoding(name = "images", contentType = "image/*")
            }
    ))
    @PostMapping(
            value = "/{exhibitionId}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "전시 리뷰 생성", description = "전시 리뷰 생성 API - 이미지 선택 안해도 생성 가능")
    public CustomResponse<ExhibitionReviewResDTO.ExReviewCreateResDTO> createExhibitionReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long exhibitionId,
            @RequestPart("request") @Valid ExhibitionReviewReqDTO.createExReviewReqDTO request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return CustomResponse.onSuccess(exhibitionReviewCommandService.createExhibitionReview(exhibitionId, currentUser.getId(), request, images));
    }

    @Operation(summary = "내 전시 리뷰 목록 조회")
    @GetMapping("/reviews/my")
    public CustomResponse<PageResponse<ExhibitionReviewResDTO.ExReviewDetailResDTO>> getMyExhibitionReviews(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(PageResponse.of(exhibitionReviewQueryService.getMyExhibitionReviews(currentUser.getId(), pageable)));

    }

    @Operation(summary = "전시 리뷰 목록 조회",
            description = "확장성을 위해 정렬을 선택 가능하게 했지만, 어떤 정렬 방법을 선택하든 무조건 최근 생성 순으로 정렬되게 설정했습니다.")
    @GetMapping("{exhibitionId}/reviews")
    public CustomResponse<PageResponse<ExhibitionReviewResDTO.ExReviewDetailResDTO>> getExhibitionReviews(
            @PathVariable Long exhibitionId,
            @Parameter(description = "정렬 기준")
            @RequestParam(defaultValue = "NEW") Sort sort,
            @Parameter(description = "페이지 번호")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 표시할 전시 리뷰 개수")
            @RequestParam(defaultValue = "8") int size){
        Pageable pageable = PageRequest.of(page, size);
        Sort resolved = Sort.NEW;
        return CustomResponse.onSuccess(PageResponse.of(exhibitionReviewQueryService.getExhibitionReviews(exhibitionId, resolved, pageable)));
    }

    @Operation(summary = "전시 리뷰 상세 조회", description = "전시 리뷰 상세 조회 api")
    @GetMapping("/reviews/{reviewId}")
    public CustomResponse<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviewDetail(
            @PathVariable("reviewId") Long exhibitionReviewId){
        return CustomResponse.onSuccess(exhibitionReviewQueryService.getExhibitionReviewDetail(exhibitionReviewId));
    }

    @Operation(summary = "전시 리뷰 삭제", description = "전시 리뷰 소프트 삭제 api")
    @DeleteMapping("/reviews/{reviewId}")
    public CustomResponse<String> deleteExhibitionReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("reviewId") Long exhibitionReviewId) {
        exhibitionReviewCommandService.deleteExhibitionReview(exhibitionReviewId, currentUser.getId());
        return CustomResponse.onSuccess("해당 전시 리뷰가 삭제되었습니다.");
    }
}
