package com.project.team5backend.domain.exhibition.controller;

import com.project.team5backend.domain.exhibition.dto.request.ExhibitionReqDTO;
import com.project.team5backend.domain.exhibition.dto.response.ExhibitionResDTO;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionCategory;
import com.project.team5backend.domain.exhibition.entity.enums.ExhibitionMood;
import com.project.team5backend.domain.exhibition.service.command.ExhibitionCommandService;
import com.project.team5backend.domain.exhibition.service.query.ExhibitionQueryService;
import com.project.team5backend.global.SwaggerBody;
import com.project.team5backend.global.apiPayload.CustomResponse;
import com.project.team5backend.global.entity.enums.Sort;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exhibitions")
@Tag(name = "Exhibition", description = "전시 관련 API")
public class ExhibitionController {

    private final ExhibitionCommandService exhibitionCommandService;
    private final ExhibitionQueryService exhibitionQueryService;

    @SwaggerBody(content = @Content(
            // request 파트만 JSON으로 강제
            encoding = {
                    @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE),
                    // 선택: 이미지도 의도 명시
                    @Encoding(name = "images", contentType = "image/*")
            }
    ))
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "전시 생성",
            description = "전시 생성하면 전시 객체가 심사 대상에 포함됩니다.<br>" +
                    "facilities -> [\"WIFI\", \"[RESTROOM]\", \"[STROLLER_RENTAL]\"]<br>" +
                    "exhibitionMood (SOLO, DATE, TRENDY, FAMILY<br>" +
                    "exhibitionCategory (PAINTING(회화), SCULPTURE_INSTALLATION(조각 설치), CRAFT_DESIGN(공예), PHOTO_MEDIA_ART(사진)<br>" +
                    "exhibitionType (PERSON, GROUP)<br>" +
                    "price -> 무료 0원 입력, 나머지 가격<br>" +
                    "startDate, endDate -> 2025-09-24<br>" +
                    "operatingStartHour, operatingEndHour -> \"12:34\"<br>")
    public CustomResponse<ExhibitionResDTO.ExhibitionCreateResDTO> createExhibition(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestPart("request") @Valid ExhibitionReqDTO.ExhibitionCreateReqDTO request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        return CustomResponse.onSuccess(exhibitionCommandService.createExhibition(request, currentUser.getId(), images));
    }

    @PostMapping("/{exhibitionId}/like")
    @Operation(summary = "전시 좋아요", description = "좋아요 없으면 등록, 있으면 취소")
    public CustomResponse<ExhibitionResDTO.ExhibitionLikeResDTO> toggleExhibitionLike(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long exhibitionId
    ) {
        return CustomResponse.onSuccess(exhibitionCommandService.toggleLike(exhibitionId, currentUser.getId()));
    }

    @GetMapping("/{exhibitionId}")
    @Operation(summary = "전시 상세 보기", description = "전시 상세 보기 api")
    public CustomResponse<ExhibitionResDTO.ExhibitionDetailResDTO> getExhibitionDetail(@PathVariable Long exhibitionId) {
        return CustomResponse.onSuccess(exhibitionQueryService.findExhibitionDetail(exhibitionId));
    }

    @Operation(summary = "전시 검색",
            description = "전시 검색 API입니다.\n"
                    + "- 한 페이지에 **4개의 전시**가 반환됩니다.\n"
                    + "- 기본 좌표는 서울시청을 중심으로 합니다.\n\n"
                    + "- 요청 파라미터 설명\n"
                    + "- `exhibitionCategory` : 전시 카테고리 (옵션)\n"
                    + "- `district` : 행정구역 (옵션)\n"
                    + "- `exhibitionMood` : 전시 분위기 (옵션)\n"
                    + "- `localDate` : 날짜 필터 (예: `2025-09-24`)\n"
                    + "- `sort` : 정렬 기준\n"
                    + "    - `POPULAR` (기본값, 리뷰 많은 순)\n"
                    + "    - `NEW` (최신순)\n"
                    + "    - `OLD` (오래된순)\n"
                    + "- `page` : 페이지 번호 (기본값 0)")
    @GetMapping("/search")
    public CustomResponse<ExhibitionResDTO.ExhibitionSearchPageResDTO> searchExhibitions(
            @RequestParam(name = "exhibitionCategory", required = false) ExhibitionCategory exhibitionCategory,
            @RequestParam(name = "distinct", required = false) String district,
            @RequestParam(name = "exhibitionMood", required = false) ExhibitionMood exhibitionMood,
            @Parameter(description = "날짜", example = "2025-09-13")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate,
            @RequestParam(defaultValue = "POPULAR") Sort sort,   // new | old | popular
            @RequestParam(name = "page", defaultValue = "0") int page
    ) {
        return CustomResponse.onSuccess(exhibitionQueryService.searchExhibitions(exhibitionCategory, district, exhibitionMood, localDate, sort, page));
    }

    @Operation(summary = "지금 뜨는 전시회", description = "현재 진행중인 전시중에서 reviewCount가 가장 높은 전시 반환")
    @GetMapping("/hot-now")
    public CustomResponse<List<ExhibitionResDTO.ExhibitionHotNowResDTO>> getExhibitionHotNow(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return CustomResponse.onSuccess(exhibitionQueryService.getHotNowExhibitions(currentUser.getId()));
    }

    @Operation(summary = "지금 뜨는, 다가오는 전시회", description = "아직 시작되지 않은 전시중에서 likeCount가 가장 높은 전시 반환")
    @GetMapping("/upcoming-popularity")
    public CustomResponse<ExhibitionResDTO.UpcomingPopularExhibitionResDTO> getUpcomingPopularExhibition() {
        return CustomResponse.onSuccess(exhibitionQueryService.getUpcomingPopularExhibition());
    }

    @Operation(summary = "지금 뜨는 지역별 전시회", description = "현재 진행중인 전시중 reviewCount가 높은, 각기 다른 지역구를 가진 전시 4개 반환")
    @GetMapping("/trending-region")
    public CustomResponse<ExhibitionResDTO.RegionalPopularExhibitionListResDTO> getRegionalPopularExhibitions() {
        return CustomResponse.onSuccess(exhibitionQueryService.getRegionalPopularExhibitions());
    }

    @Operation(summary = "artie 추천 전시회", description = "artie 추천 전시 4개 반환 - 하루 단위로 업데이트")
    @GetMapping("/artie-recommendation")
    public CustomResponse<List<ExhibitionResDTO.ArtieRecommendationResDTO>> getTodayArtieRecommendations(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return CustomResponse.onSuccess(exhibitionQueryService.getTodayArtieRecommendations(currentUser.getId()));
    }

    @Operation(summary = "전시 삭제", description = "전시가 삭제된 전시로 변경하는 api")
    @DeleteMapping("/{exhibitionId}")
    public CustomResponse<String> deleteExhibition(
            @AuthenticationPrincipal CurrentUser currentUser,
            @PathVariable Long exhibitionId){
        exhibitionCommandService.deleteExhibition(exhibitionId, currentUser.getId());
        return CustomResponse.onSuccess("해당 전시가 삭제되었습니다.");
    }

    @Operation(summary = "찜한 전시", description = "내가 찜한 전시의 정보를 보여준다")
    @GetMapping("/interest")
    public CustomResponse<PageResponse<ExhibitionResDTO.ExhibitionDetailResDTO>> getInterestedExhibitions(
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return CustomResponse.onSuccess(PageResponse.of(exhibitionQueryService.getInterestedExhibitions(currentUser.getId(), pageable)));
    }
}
