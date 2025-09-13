package com.project.team5backend.domain.review.exhibition.controller;

import com.project.team5backend.domain.review.exhibition.dto.request.ExhibitionReviewReqDTO;
import com.project.team5backend.domain.review.exhibition.dto.response.ExhibitionReviewResDTO;
import com.project.team5backend.domain.review.exhibition.service.command.ExhibitionReviewCommandService;
import com.project.team5backend.domain.review.exhibition.service.query.ExhibitionReviewQueryService;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "리뷰 생성", description = "리뷰 생성 API")
    public CustomResponse<ExhibitionReviewResDTO.ExReviewCreateResDTO> createExhibitionReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long exhibitionId,
            @RequestPart("request") @Valid ExhibitionReviewReqDTO.createExReviewReqDTO request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return CustomResponse.onSuccess(exhibitionReviewCommandService.createExhibitionReview(exhibitionId, currentUser.getId(), request, images));
    }


    @Operation(summary = "리뷰 목록 조회", description = "리뷰 목록 조회 api")
    @GetMapping("{exhibitionId}/reviews")
    public CustomResponse<Page<ExhibitionReviewResDTO.ExReviewDetailResDTO>> getExhibitionReviews(
            @PathVariable Long exhibitionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ExhibitionReviewResDTO.ExReviewDetailResDTO> reviewPage = exhibitionReviewQueryService.getExhibitionReviewList(exhibitionId, pageable);
        return CustomResponse.onSuccess(reviewPage);
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 상세 조회 api")
    @GetMapping("/reviews/{reviewId}")
    public CustomResponse<ExhibitionReviewResDTO.ExReviewDetailResDTO> getExhibitionReviewDetail(
            @PathVariable("reviewId") Long exhibitionReviewId){
        return CustomResponse.onSuccess(exhibitionReviewQueryService.getExhibitionReviewDetail(exhibitionReviewId));
    }
    @Operation(summary = "리뷰 삭제", description = "리뷰 소프트 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public CustomResponse<String> deleteExhibitionReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("reviewId") Long exhibitionReviewId) {
        exReviewCommandService.deleteExhibitionReview(exhibitionReviewId, currentUser.getEmail());
        return CustomResponse.onSuccess("해당 전시 리뷰가 삭제되었습니다.");
    }
}
