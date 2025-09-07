package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.space.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.space.service.query.SpaceQueryService;
import com.project.team5backend.domain.user.repository.UserRepository;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import com.project.team5backend.global.util.ImageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Tag(name = "공간")
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;
    private final UserRepository userRepository;

    @SwaggerBody(content = @Content(
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
            }
    ))
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "전시 공간 등록", description = "등록시 공간 객체가 심사 대상에 포함됩니다.")
    public CustomResponse<SpaceResDTO.CreateSpaceResDTO> registerSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestPart("request") @Valid SpaceReqDTO.CreateSpaceReqDTO createSpaceReqDTO,
            @RequestPart("images") List<MultipartFile> images
    ) {
        ImageUtils.validateImages(images); // 이미지 검증 (개수, null 여부)
        SpaceResDTO.CreateSpaceResDTO createSpaceResDTO = spaceCommandService.createSpace(createSpaceReqDTO, currentUser.getEmail(), images);
        return CustomResponse.onSuccess(createSpaceResDTO);
    }

//    @Operation(summary = "전시 공간 목록 조회")
//    @GetMapping
//    public CustomResponse<List<SpaceResponse.SpaceSearchResponse>> getSpaces() {
//        List<SpaceResponse.SpaceSearchResponse> spaces = spaceQueryService.getApprovedSpaces();
//        return CustomResponse.onSuccess(spaces);
//    }
//
//    @Operation(summary = "전시 공간 검색")
//    @GetMapping("/search")
//    public CustomResponse<SpaceResponse.SpaceSearchPageResponse> searchSpaces(
//            @RequestParam(name = "startDate", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(name = "endDate", required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            @RequestParam(name = "district", required = false) String district,
//            @RequestParam(name = "size", required = false) SpaceSize size,
//            @RequestParam(name = "type", required = false) SpaceType type,
//            @RequestParam(name = "mood", required = false) SpaceMood mood,
//            @RequestParam(name = "page", defaultValue = "0") int page
//            ) {
//        return CustomResponse.onSuccess(
//                spaceQueryService.searchSpaces(startDate, endDate, district, size, type, mood, page)
//        );
//    }
//    @Operation(summary = "전시 공간 상세 조회")
//    @GetMapping("/{spaceId}")
//    public CustomResponse<SpaceResponse.SpaceDetailResponse> getSpaceDetails(@PathVariable Long spaceId) {
//        SpaceResponse.SpaceDetailResponse spaceDetail = spaceQueryService.getSpaceDetails(spaceId);
//        return CustomResponse.onSuccess(spaceDetail);
//    }
//    @Operation(summary = "전시 공간 좋아요 / 좋아요 취소")
//    @PostMapping("/{spaceId}/like")
//    public CustomResponse<Map<String, Boolean>> toggleLike(@PathVariable Long spaceId,
//                                                           @AuthenticationPrincipal CurrentUser currentUser) {
//        Long userId = currentUser.getId();
//        boolean liked = spaceCommandService.toggleLike(spaceId, userId);
//        return CustomResponse.onSuccess(Map.of("liked", liked));
//    }
//    @Operation(summary = "전시 공간 정보 삭제")
//    @DeleteMapping("/{spaceId}")
//    public CustomResponse<Void> deleteSpace(@PathVariable Long spaceId) {
//        spaceCommandService.deleteSpace(spaceId);
//        return CustomResponse.onSuccess(null);    }
}