package com.project.team5backend.domain.review.space.controller;


import com.project.team5backend.domain.review.space.dto.request.SpaceReviewReqDTO;
import com.project.team5backend.domain.review.space.dto.response.SpaceReviewResDTO;
import com.project.team5backend.domain.review.space.service.command.SpaceReviewCommandService;
import com.project.team5backend.domain.review.space.service.query.SpaceReviewQueryService;
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
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Tag(name = "SpaceReview",description = "공간 리뷰 관련 API")
public class SpaceReviewController {

    private final SpaceReviewCommandService spaceReviewCommandService;
    private final SpaceReviewQueryService spaceReviewQueryService;

    @SwaggerBody(content = @Content(
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
            }
    ))
    @PostMapping(
            value = "/{spaceId}/reviews",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "공간 리뷰 생성")
    public CustomResponse<SpaceReviewResDTO.CreateSpaceReviewResDTO> createSpaceReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId,
            @RequestPart("request") @Valid SpaceReviewReqDTO.CreateSpaceReviewReqDTO request,
            @RequestPart(name = "images", required = false) List<MultipartFile> images
    ) {
        return CustomResponse.onSuccess(spaceReviewCommandService.createSpaceReview(spaceId, currentUser.getId(), request, images));
    }

    @Operation(summary = "공간 리뷰 목록 조회")
    @GetMapping("{spaceId}/reviews")
    public CustomResponse<Page<SpaceReviewResDTO.DetailSpaceReviewResDTO>> getSpaceReviewList(
            @PathVariable Long spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return CustomResponse.onSuccess(spaceReviewQueryService.getSpaceReviewList(spaceId, pageable));
    }

    @Operation(summary = "공간 리뷰 상세 조회")
    @GetMapping("reviews/{reviewId}")
    public CustomResponse<SpaceReviewResDTO.DetailSpaceReviewResDTO> getSpaceReviewDetail(@PathVariable("reviewId") Long spaceReviewId) {
        return CustomResponse.onSuccess(spaceReviewQueryService.getSpaceReviewDetail(spaceReviewId));
    }

    @Operation(summary = "전시 공간 리뷰 삭제")
    @DeleteMapping("reviews/{reviewId}")
    public CustomResponse<String> deleteSpaceReview(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable("reviewId") Long spaceReviewId) {
        spaceReviewCommandService.deleteSpaceReview(spaceReviewId, currentUser.getId());
        return CustomResponse.onSuccess("해당 공간 리뷰가 삭제되었습니다.");
    }
}
