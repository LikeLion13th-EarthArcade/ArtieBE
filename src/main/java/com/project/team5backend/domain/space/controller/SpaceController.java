package com.project.team5backend.domain.space.controller;

import com.project.team5backend.domain.space.dto.request.SpaceReqDTO;
import com.project.team5backend.domain.space.dto.response.SpaceResDTO;
import com.project.team5backend.domain.space.entity.enums.SpaceMood;
import com.project.team5backend.domain.space.entity.enums.SpaceSize;
import com.project.team5backend.domain.space.entity.enums.SpaceType;
import com.project.team5backend.domain.space.service.command.SpaceCommandService;
import com.project.team5backend.domain.space.service.query.SpaceQueryService;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.entity.enums.Sort;
import com.project.team5backend.global.security.userdetails.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/spaces")
@RequiredArgsConstructor
@Tag(name = "Space", description = "공간 관련 API")
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
    @Operation(summary = "전시 공간 등록",
            description = "등록시 공간 객체가 심사 대상에 포함됩니다.<br>" +
                    "사업자 인증 api가 선행되어 성공해야함 -> 그렇지 않으면 예외<br>" +
                    "facilities -> [\"WIFI\", \"[RESTROOM]\", \"[STROLLER_RENTAL]\"]<br>" +
                    "operatingStartHour, operatingEndHour -> \"12:34\" (스웨거 기본 형식 무시)<br>" +
                    "spaceMood (WHITE_BOX, INDUSTRIAL, VINTAGE_CLASSIC, NATURE_LIGHT, FOCUSED_LIGHTING)<br>" +
                    "spaceSize (SMALL(~10), MEDIUM_SMALL(~30), MEDIUM(~50), LARGE(50~)<br>" +
                    "spaceType (EXHIBITION(전시), POPUP_STORE(팝업), EXPERIENCE_EXHIBITION(체험 전시)")
    public CustomResponse<SpaceResDTO.SpaceCreateResDTO> createSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestPart("request") @Valid SpaceReqDTO.SpaceCreateReqDTO spaceCreateReqDTO,
            @RequestParam(value = "businessLicenseFile") MultipartFile businessLicenseFile,
            @RequestParam(value = "buildingRegisterFile") MultipartFile buildingRegisterFile,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return CustomResponse.onSuccess(HttpStatus.CREATED, spaceCommandService.createSpace(spaceCreateReqDTO, currentUser.getId(), businessLicenseFile, buildingRegisterFile, images));
    }

    @PostMapping("/{spaceId}/like")
    @Operation(summary = "공간 좋아요", description = "좋아요 없으면 등록, 있으면 취소")
    public CustomResponse<SpaceResDTO.SpaceLikeResDTO> toggleSpaceLike(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId
    ) {
        return CustomResponse.onSuccess(spaceCommandService.toggleLike(spaceId, currentUser.getId()));
   }

    @Operation(summary = "전시 공간 상세 조회", description = "공간 id를 이용해 공간의 상세 정보 조회")
    @GetMapping("/{spaceId}")
    public CustomResponse<SpaceResDTO.SpaceDetailResDTO> getSpaceDetail(@PathVariable Long spaceId) {
        return CustomResponse.onSuccess(spaceQueryService.getSpaceDetail(spaceId));
    }

    @Operation(
            summary = "공간 검색",
            description = "공간 검색 API입니다.\n"
                    + "- 한 페이지에 **4개의 공간**이 반환됩니다.\n"
                    + "- 기본 좌표는 서울시청을 중심으로 합니다.\n\n"
                    + "- 요청 파라미터 설명\n"
                    + "- `requestedStartDate` : 대여 시작일 (예: `2025-09-12`)\n"
                    + "- `requestedEndDate` : 대여 종료일 (예: `2025-09-13`)\n"
                    + "- `district` : 행정구역 (옵션)\n"
                    + "- `size` : 공간 크기 (옵션)\n"
                    + "- `type` : 공간 유형 (옵션)\n"
                    + "- `mood` : 공간 분위기 (옵션)\n"
                    + "- `facilities` : 시설 목록 (예: WIFI, RESTROOM, STROLLER_RENTAL)\n"
                    + "- `sort` : 정렬 기준\n"
                    + "    - `POPULAR` (기본값, 리뷰 많은 순)\n"
                    + "    - `NEW` (최신순)\n"
                    + "    - `OLD` (오래된순)\n"
                    + "- `page` : 페이지 번호 (기본값 0)"
    )    @GetMapping("/search")
    public CustomResponse<SpaceResDTO.SpaceSearchPageResDTO> searchSpaces(
            @Parameter(description = "대여 시작일", example = "2025-09-12")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestedStartDate,
            @Parameter(description = "대여 종료일", example = "2025-09-13")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestedEndDate,
            @RequestParam(name = "distinct", required = false) String district,
            @RequestParam(name = "size", required = false) SpaceSize size,
            @RequestParam(name = "type", required = false) SpaceType type,
            @RequestParam(name = "mood", required = false) SpaceMood mood,
            @Parameter(
                    description = "시설 목록 (예: WIFI, RESTROOM, STROLLER_RENTAL)",
                    array = @ArraySchema(schema = @Schema(type = "string"))
            )
            @RequestParam(name = "facilities", required = false) List<String> facilities, // 스웨거 편의성을 위해 enum 설정
            @RequestParam(defaultValue = "POPULAR") Sort sort,
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(spaceQueryService.searchSpace(requestedStartDate, requestedEndDate, district, size, type, mood, facilities, sort, page));
    }

    @Operation(summary = "전시 공간 삭제")
    @DeleteMapping("/{spaceId}")
    public CustomResponse<String> deleteSpace(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long spaceId) {
        spaceCommandService.deleteSpace(spaceId, currentUser.getId());
        return CustomResponse.onSuccess(HttpStatus.NO_CONTENT, "공간이 삭제되었습니다.");
    }
}