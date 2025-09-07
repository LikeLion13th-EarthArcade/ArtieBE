package com.project.team5backend.domain.space.space.controller;

import com.project.team5backend.domain.exhibition.exhibition.dto.response.ExhibitionResDTO;
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
@Tag(name = "Space")
public class SpaceController {

    private final SpaceCommandService spaceCommandService;
    private final SpaceQueryService spaceQueryService;

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
        SpaceResDTO.CreateSpaceResDTO createSpaceResDTO = spaceCommandService.createSpace(createSpaceReqDTO, currentUser.getId(), images);
        return CustomResponse.onSuccess(createSpaceResDTO);
    }

    @PostMapping("/{spaceId}/like")
    @Operation(summary = "공간 좋아요", description = "좋아요 없으면 등록, 있으면 취소")
    public CustomResponse<SpaceResDTO.LikeSpaceResDTO> likeSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId
    ) {
        return CustomResponse.onSuccess(spaceCommandService.likeSpace(spaceId, currentUser.getId()));
    }

    @Operation(summary = "전시 공간 상세 조회")
    @GetMapping("/{spaceId}")
    public CustomResponse<SpaceResDTO.DetailSpaceResDTO> getSpaceDetails(@PathVariable Long spaceId) {
        return CustomResponse.onSuccess(spaceQueryService.getSpaceDetail(spaceId));
    }

    @Operation(summary = "전시 공간 삭제")
    @DeleteMapping("/{spaceId}")
    public CustomResponse<String> deleteSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId, currentUser.getId());
        return CustomResponse.onSuccess("공간이 삭제되었습니다.");
    }


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
}